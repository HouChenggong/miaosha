package cn.monitor4all.miaoshaservice.debug.consumer;

import cn.monitor4all.miaoshadao.dao.MailDto;
import cn.monitor4all.miaoshadao.dao.StockOrder;
import cn.monitor4all.miaoshadao.mapper.StockOrderMapper;
import cn.monitor4all.miaoshaservice.debug.DebugRabbitConsts;
import cn.monitor4all.miaoshaservice.debug.mail.DebugMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiyou
 * debug rabbitMQ消费服务
 */
@Service
@Slf4j
public class DebugRabbitConsumer {

    @Autowired
    private DebugMailService mailService;


    @Autowired
    private StockOrderMapper stockOrderMapper;

    /**
     * 秒杀异步邮件通知-接收消息
     * queues接受的队列
     * containerFactory接受的工厂，现在用的是DebugRabbitmqConfig里面的单一消费者模式的工厂
     */
    @RabbitListener(queues = {DebugRabbitConsts.MAIL_SUCCESS_QUEUE}, containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(StockOrder info) {
        try {
            log.info("秒杀异步邮件通知-接收消息:{}", info);

            //TODO:真正的发送邮件....
            log.info("这里应该是真正发送邮件的逻辑..........如果发送成功，可以更新下数据库的字段，表示发送成功，否则就执行相关邮件发送失败逻辑");
            mailService.sendSimpleEmail(new MailDto("邮件", "这是邮件内容", new String[]{"xxx@qq.com", "xbbb@qq.com"}));
        } catch (Exception e) {
            log.error("秒杀异步邮件通知-接收消息-发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 用户秒杀成功后超时未支付-监听者
     *
     * @param info
     */
    @RabbitListener(queues = {DebugRabbitConsts.MAIL_SUCCESS_DEAD_REAL_QUEUE}, containerFactory = "singleListenerContainer")
    public void consumeExpireOrder(StockOrder info) {
        try {
            log.info("用户秒杀成功后超时未支付-监听者-接收消息:{}", info);

            if (info != null) {
                StockOrder entity = stockOrderMapper.selectByPrimaryKey(info.getId());
                // 到时间了判断订单是否存于未支付状态
                if (entity != null && entity.getStatus() != null && entity.getStatus() == 0) {
                    log.info("用户超时未支付，发送消息并把订单取消");
                    stockOrderMapper.expireOrder(info.getId());
                }
            }
        } catch (Exception e) {
            log.error("用户秒杀成功后超时未支付-监听者-发生异常：", e.fillInStackTrace());
        }
    }
}
