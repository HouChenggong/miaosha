package cn.monitor4all.miaoshaservice.service;

import cn.monitor4all.miaoshadao.dao.Stock;
import cn.monitor4all.miaoshadao.dao.StockOrder;
import cn.monitor4all.miaoshadao.dao.User;
import cn.monitor4all.miaoshadao.mapper.StockOrderMapper;
import cn.monitor4all.miaoshadao.mapper.UserMapper;
import cn.monitor4all.miaoshadao.utils.CacheKey;
import cn.monitor4all.miaoshaservice.debug.send.DebugRabbitSenderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockOrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DebugRabbitSenderService senderService;


    @Override
    public int createWrongOrder(int sid) {
        //校验库存
        Stock stock = checkStock(sid);
        //扣库存
        saleStock(stock);
        //创建订单
        int id = createOrder(stock);
        return id;
    }

    @Override
    public int createOptimisticOrder(int sid) {
        //校验库存
        Stock stock = checkStock(sid);
        //乐观锁更新库存
        saleStockOptimistic(stock);
        //创建订单
        int id = createOrder(stock);
        return stock.getCount() - (stock.getSale() + 1);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public int createPessimisticOrder(int sid) {
        //校验库存(悲观锁for update)
        Stock stock = checkStockForUpdate(sid);
        //更新库存
        saleStock(stock);
        //创建订单
        int id = createOrder(stock);
        return stock.getCount() - (stock.getSale());
    }

    @Override
    public int createVerifiedOrder(Integer sid, Integer userId, String verifyHash) throws Exception {

        // 验证是否在抢购时间内
        LOGGER.info("请自行验证是否在抢购时间内,假设此处验证成功");

        // 验证hash值合法性
        String hashKey = CacheKey.HASH_KEY.getKey() + "_" + sid + "_" + userId;
        System.out.println(hashKey);
        String verifyHashInRedis = stringRedisTemplate.opsForValue().get(hashKey);
        if (!verifyHash.equals(verifyHashInRedis)) {
            throw new Exception("hash值与Redis中不符合");
        }
        LOGGER.info("验证hash值合法性成功");

        // 检查用户合法性
        User user = userMapper.selectByPrimaryKey(userId.longValue());
        if (user == null) {
            throw new Exception("用户不存在");
        }
        LOGGER.info("用户信息验证成功：[{}]", user.toString());

        // 检查商品合法性
        Stock stock = stockService.getStockById(sid);
        if (stock == null) {
            throw new Exception("商品不存在");
        }
        LOGGER.info("商品信息验证成功：[{}]", stock.toString());

        //乐观锁更新库存
        saleStockOptimistic(stock);
        LOGGER.info("乐观锁更新库存成功");

        //创建订单
        createOrderWithUserInfo(stock, userId);
        LOGGER.info("创建订单成功");

        return stock.getCount() - (stock.getSale() + 1);
    }

    @Override
    public int createOptimisticOrderAndSendMsg(int sid) {
        //校验库存
        Stock stock = checkStock(sid);
        //乐观锁更新库存
        saleStockOptimistic(stock);
        //创建订单
        int id = createOrder(stock);
        //发送消息
        senderService.sendKillSuccessEmailMsg(id);
        //入死信队列，如果超时，会自动发送消息
        senderService.sendKillSuccessOrderExpireMsg(id);
        int success = stock.getCount() - (stock.getSale() + 1);
        return success;
    }

    @Override
    public int redisKill(int sid, Integer userId) {
        //校验库存
        Stock stock = checkStock(sid);
        int total = orderMapper.selectBySidAndUserId(sid, userId);
        if (total > 0) {
            log.error("当前用户已经秒杀过了" + userId);
            return 0;
        } else {
            System.out.println("过来了一个线程" + userId + "____"+System.currentTimeMillis());
        }
        //TODO:借助Redis的原子操作实现分布式锁-对共享操作-资源进行控制
        ValueOperations valueOperations = stringRedisTemplate.opsForValue();
        final String key = new StringBuffer().append(sid).append(userId).append("-RedisLock").toString();
        final String value = RandomUtils.nextInt() + "";
        //lua脚本提供“分布式锁服务”，就可以写在一起
        Boolean cacheRes = valueOperations.setIfAbsent(key, value);
        if (cacheRes) {
            stringRedisTemplate.expire(key, 30, TimeUnit.SECONDS);
            try {
                //扣库存,不用乐观锁，也不用悲观锁
                saleStock(stock);
                //创建订单
                int id = createOrderWithUserInfo(stock, userId);
                log.error("当前用户秒杀成功" + userId);
                //发送消息
                senderService.sendKillSuccessEmailMsg(id);
                //入死信队列，如果超时，会自动发送消息
                senderService.sendKillSuccessOrderExpireMsg(id);
                int success = stock.getCount() - (stock.getSale() + 1);
                return success;
            } catch (Exception e) {
                throw new RuntimeException("抢购失败");
            } finally {
                if (value.equals(valueOperations.get(key).toString())) {
                    stringRedisTemplate.delete(key);
                }
            }
        }
        return 0;
    }

    @Override
    public int checkOrderBySidAndUserId(int sid, Integer userId) {
        return 0;
    }

    /**
     * 检查库存
     *
     * @param sid
     * @return
     */
    private Stock checkStock(int sid) {
        Stock stock = stockService.getStockById(sid);
        if (stock.getSale().equals(stock.getCount())) {
            throw new RuntimeException("库存不足");
        }
        return stock;
    }

    /**
     * 检查库存 ForUpdate
     *
     * @param sid
     * @return
     */
    private Stock checkStockForUpdate(int sid) {
        Stock stock = stockService.getStockByIdForUpdate(sid);
        if (stock.getSale().equals(stock.getCount())) {
            throw new RuntimeException("库存不足");
        }
        return stock;
    }

    /**
     * 更新库存
     *
     * @param stock
     */
    private void saleStock(Stock stock) {
        stock.setSale(stock.getSale() + 1);
        stockService.updateStockById(stock);
    }

    /**
     * 更新库存 乐观锁
     *
     * @param stock
     */
    private void saleStockOptimistic(Stock stock) {
        LOGGER.info("查询数据库，尝试更新库存");
        int count = stockService.updateStockByOptimistic(stock);
        if (count == 0) {
            throw new RuntimeException("乐观锁并发更新库存失败");
        }
    }

    /**
     * 创建订单
     *
     * @param stock
     * @return
     */
    private int createOrder(Stock stock) {
        StockOrder order = new StockOrder();
        order.setSid(stock.getId());
        order.setName(stock.getName());
        order.setStatus(0);
        orderMapper.insertSelective(order);
        return order.getId();
    }

    /**
     * 创建订单：保存用户信息
     *
     * @param stock
     * @return
     */
    private int createOrderWithUserInfo(Stock stock, Integer userId) {
        StockOrder order = new StockOrder();
        order.setSid(stock.getId());
        order.setName(stock.getName());
        order.setUserId(userId);
        //未支付的状态
        order.setStatus(0);
        return orderMapper.insertSelective(order);
    }
}
