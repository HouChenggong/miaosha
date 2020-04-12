package cn.monitor4all.miaoshaservice.service.distributed;

import cn.monitor4all.miaoshadao.dao.Stock;
import cn.monitor4all.miaoshadao.dao.StockOrder;
import cn.monitor4all.miaoshadao.mapper.StockMapper;
import cn.monitor4all.miaoshadao.mapper.StockOrderMapper;
import cn.monitor4all.miaoshadao.mapper.UserMapper;
import cn.monitor4all.miaoshadao.response.ServerResponse;
import cn.monitor4all.miaoshaservice.debug.send.DebugRabbitSenderService;
import cn.monitor4all.miaoshaservice.exception.KillException;
import cn.monitor4all.miaoshaservice.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author xiyou
 * redis测试分布式锁相关内容
 */

@Service
@Slf4j
public class DistributedOrderServiceImpl implements IDistributedOrderService {

    /**
     * 库存表
     */
    @Autowired
    private StockMapper stockMapper;

    /**
     * 用户订单表
     */
    @Autowired
    private StockOrderMapper orderMapper;

    /**
     * 用户表
     */
    @Autowired
    private UserMapper userMapper;


    /**
     * redis操作
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * rabbitmq
     */
    @Autowired
    private DebugRabbitSenderService senderService;

    /**
     * 商品库存表的service
     */
    @Autowired
    private StockService stockService;


    @Autowired
    private RedissonClient redissonClient;

    /**
     * 用redis秒杀订单
     * 注意由于子方法都是如果除了错误向上抛异常，所以，大方法里面千万不要catch异常
     * 如果catch了异常，就会出现异常被吃，然后事务回滚失败的情况
     *
     * @param sid
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse redisKill(Integer sid, Integer userId) {
        Stock stock = doUserStockjudge(sid, userId);
        //TODO:借助Redis的原子操作实现分布式锁-对共享操作-资源进行控制
        ValueOperations valueOperations = stringRedisTemplate.opsForValue();
        final String key = new StringBuffer().append(sid).append(userId).append("-RedisLock").toString();
        final String value = RandomUtils.nextInt() + "";
        //lua脚本提供“分布式锁服务”，就可以写在一起
        Boolean cacheRes = valueOperations.setIfAbsent(key, value);
        if (cacheRes) {
            stringRedisTemplate.expire(key, 10, TimeUnit.SECONDS);
            try {
                return killMainMethod(userId, stock);
            } finally {
                sendMessage(stock.getId());
                if (value.equals(valueOperations.get(key).toString())) {
                    stringRedisTemplate.delete(key);
                }
            }

        } else {
            return ServerResponse.createByErrorMessage("未获取到分布式锁，导致秒杀失败" + userId);
        }

    }

    /**
     * 校验库存，并校验用户是否已经秒杀过了
     *
     * @param sid
     * @param userId
     * @return
     */
    private Stock doUserStockjudge(Integer sid, Integer userId) {
        //校验库存
        Stock stock = checkStock(sid, userId);
        int total = orderMapper.selectBySidAndUserId(sid, userId);
        if (total > 0) {
            log.error("当前用户已经秒杀过了" + userId);
            throw new KillException("当前用户已经秒杀过了,导致秒杀失败" + userId);
        } else {
            System.out.println("过来了一个线程" + userId + "____" + System.currentTimeMillis());
        }
        return stock;
    }

    /**
     * 秒杀的核心业务逻辑
     *
     * @param userId
     * @param stock
     * @return
     */
    private ServerResponse killMainMethod(Integer userId, Stock stock) {
        //乐观锁扣减库存
        saleStockOptimistic(stock, userId);
        //创建订单
        int orderId = createOrderWithUserInfo(stock, userId);
        log.info("当前用户秒杀成功" + userId);
        return ServerResponse.createBySuccess("用户秒杀成功，订单为：" + orderId);
    }

    /**
     * 注意由于子方法都是如果除了错误向上抛异常，所以，大方法里面千万不要catch异常
     * 如果catch了异常，就会出现异常被吃，然后事务回滚失败的情况
     *
     * @param sid
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse redissionKill(Integer sid, Integer userId) {
        //校验库存和用户是否秒杀过
        Stock stock = doUserStockjudge(sid, userId);

        final String locakKey = new StringBuffer().append(sid).append(userId).append("--redissionLock").toString();
        RLock lock = redissonClient.getLock(locakKey);
        Boolean getLock = true;
        //第一个try是尝试获取分布式锁
        try {
            //尝试获取锁的时间是30秒
            getLock = lock.tryLock(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            getLock = false;
        }
        //第二个try是如果获取到了分布式，应该执行的逻辑
        try {
            if (getLock) {
                return killMainMethod(userId, stock);
            } else {
                log.error("单个用户多次秒杀，这次未获取锁，秒杀失败" + userId);
                return ServerResponse.createByErrorMessage("未获取锁，秒杀失败");
            }
        } finally {
            lock.unlock();
            sendMessage(stock.getId());
        }
    }


    /**
     * 检查库存
     *
     * @param sid
     * @param userId
     * @return
     */
    private Stock checkStock(int sid, Integer userId) {
        Stock stock = stockMapper.selectByPrimaryKey(sid);
        if (stock.getSale() >= (stock.getCount())) {
            throw new KillException("库存不足导致秒杀失败,用户id是：" + userId);
        }
        return stock;
    }


    /**
     * 更新库存 乐观锁
     *
     * @param stock
     * @param userId
     */
    private void saleStockOptimistic(Stock stock, Integer userId) {
        log.info("查询数据库，尝试更新库存");
        int count = stockService.updateStockByOptimistic(stock);
        if (count == 0) {
            throw new KillException("乐观锁并发更新库存失败导致秒杀失败,用户id是：" + userId);
        }
    }


    /**
     * 创建订单：保存用户信息
     *
     * @param stock
     * @param userId
     * @return
     */
    private int createOrderWithUserInfo(Stock stock, Integer userId) {
        StockOrder order = new StockOrder();
        order.setSid(stock.getId());
        order.setName(stock.getName());
        order.setUserId(userId);
        //未支付的状态
        order.setStatus(0);
        order.setCreateTime(new Date());
        orderMapper.insertSelective(order);
        if (order.getId() == null) {
            throw new KillException("创建订单失败导致秒杀失败,用户id是：" + userId);
        }
        return order.getId();
    }

    /**
     * 发送消息执行的逻辑
     * 因为这个代码里面如果有异常，会自动捕获，所以这两个代码不会产生异常
     *
     * @param orderId
     */
    public void sendMessage(Integer orderId) {
        //发送消息
        senderService.sendKillSuccessEmailMsg(orderId);
        //入死信队列，如果超时，会自动发送消息
        senderService.sendKillSuccessOrderExpireMsg(orderId);
    }
}
