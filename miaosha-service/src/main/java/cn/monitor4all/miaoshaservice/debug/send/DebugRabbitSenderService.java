package cn.monitor4all.miaoshaservice.debug.send;

import cn.monitor4all.miaoshadao.dao.StockOrder;
import cn.monitor4all.miaoshadao.mapper.StockOrderMapper;
import cn.monitor4all.miaoshaservice.debug.DebugRabbitConsts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiyou
 * debug rabbitMQ发送消息服务
 */
@Slf4j
@Service
public class DebugRabbitSenderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 秒杀成功的订单mapper主要用来查询订单信息
     */

    @Autowired
    private StockOrderMapper orderMapper;


    /**
     * 秒杀成功异步发送邮件通知消息
     *
     * @param orderNo 秒杀成功之后的订单编号
     */
    public void sendKillSuccessEmailMsg(int orderNo) {
        log.info("秒杀成功异步发送邮件、短信通知消息-准备发送消息：{}", orderNo);

        try {
            StockOrder info = orderMapper.selectByPrimaryKey(orderNo);
            if (info != null) {
                //TODO:rabbitmq发送消息的逻辑
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //绑定交换机
                rabbitTemplate.setExchange(DebugRabbitConsts.MAIL_SUCCESS_EXCHANGE);
                //绑定路由和路由之后，他会把它绑定到指定的队列
                rabbitTemplate.setRoutingKey(DebugRabbitConsts.MAIL_SUCCESS_ROUTING);


                //TODO：将info充当消息发送至队列
                rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        MessageProperties messageProperties = message.getMessageProperties();
                        //保证消息可靠性，进行持久化
                        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        //设置消息头，并指定确切的类型，这样消费者就可以直接用对象接收
                        messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, StockOrder.class);
                        return message;
                    }
                });
            }

        } catch (Exception e) {
            log.error("秒杀成功异步发送邮件、短信通知消息-发生异常，消息为：{}", orderNo, e.fillInStackTrace());
        }
    }
    /**
     * 秒杀成功后生成抢购订单-发送信息入死信队列，等待着一定时间失效超时未支付的订单
     * @param orderCode
     */
    public void sendKillSuccessOrderExpireMsg(final int  orderCode){
        try {
            log.info("死信队列中添加了一条消息：{}", orderCode);
                StockOrder info = orderMapper.selectByPrimaryKey(orderCode);
                if (info!=null){
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(DebugRabbitConsts.MAIL_SUCCESS_DEAD_TTL_EXCHANGE);
                    rabbitTemplate.setRoutingKey(DebugRabbitConsts.MAIL_SUCCESS_DEAD_TTL_ROUTING);
                    rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            MessageProperties mp=message.getMessageProperties();
                            mp.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            mp.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,StockOrder.class);

                            //TODO：动态设置TTL(为了测试方便，暂且设置10s)
                            mp.setExpiration (DebugRabbitConsts.TTL_TIME);
                            return message;
                        }
                    });
                }
        }catch (Exception e){
            log.error("秒杀成功后生成抢购订单-发送信息入死信队列，等待着一定时间失效超时未支付的订单-发生异常，消息为：{}",orderCode,e.fillInStackTrace());
        }
    }
}
