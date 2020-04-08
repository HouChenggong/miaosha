package cn.monitor4all.miaoshaservice.debug;


import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 通用化 Rabbitmq 配置
 *
 * @author xiyou
 */
@Configuration
@Slf4j
public class DebugRabbitmqConfig {


    /**
     * 连接工厂
     */
    @Autowired
    private CachingConnectionFactory connectionFactory;

    /**
     * 消费者实例监听工厂
     */
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     * 单一消费者
     *
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //传输格式JSON
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        //每次拉取一条
        factory.setPrefetchCount(1);
        factory.setTxSize(1);
        return factory;
    }

    /**
     * 多个消费者，提高吞吐量
     *
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //确认消费模式-NONE（默认的）
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        //每次拉取10条消息
        factory.setPrefetchCount(10);
        return factory;
    }

    /**
     * 发送消息的核心
     *
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        //发送消息确认模式
        connectionFactory.setPublisherConfirms(true);
        //对于发布确认，template要求CachingConnectionFactory的publisherConfirms属性设置为true。
        //如果要消息确认，则必须实现Callback，也就是下面的
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.warn("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message);
            }
        });
        return rabbitTemplate;
    }


    // ----------------------------------------------------秒杀成功的MQ-


    //构建异步发送邮箱通知的消息模型
    @Bean
    public Queue successEmailQueue() {
        //名字和持久化
        return new Queue(DebugRabbitConsts.MAIL_SUCCESS_QUEUE, true);
    }

    /**
     * 持久化并且不自动删除
     *
     * @return
     */
    @Bean
    public TopicExchange successEmailExchange() {
        //名字、持久化、自动删除
        return new TopicExchange(DebugRabbitConsts.MAIL_SUCCESS_EXCHANGE, true, false);
    }

    /**
     * 将一个交换机和路由绑定到一个队列中
     *
     * @return
     */
    @Bean
    public Binding successEmailBinding() {
        return BindingBuilder.bind(successEmailQueue()).to(successEmailExchange()).with(DebugRabbitConsts.MAIL_SUCCESS_ROUTING);
    }
    // ----------------------------------------------------秒杀成功的MQ-


    //构建秒杀成功之后-订单超时未支付的死信队列消息模型

    @Bean
    public Queue successKillDeadQueue() {
        Map<String, Object> argsMap = Maps.newHashMap();
        argsMap.put("x-dead-letter-exchange", DebugRabbitConsts.MAIL_SUCCESS_DEAD_EXCHANGE);
        argsMap.put("x-dead-letter-routing-key", DebugRabbitConsts.MAIL_SUCCESS_DEAD_ROUTING);
        return new Queue(DebugRabbitConsts.MAIL_SUCCESS_DEAD_QUEUE, true, false, false, argsMap);
    }

    //基本交换机
    @Bean
    public TopicExchange successKillDeadProdExchange() {
        return new TopicExchange(DebugRabbitConsts.MAIL_SUCCESS_DEAD_TTL_EXCHANGE, true, false);
    }

    //创建基本交换机+基本路由 -> 死信队列 的绑定
    @Bean
    public Binding successKillDeadProdBinding() {
        return BindingBuilder.bind(successKillDeadQueue()).to(successKillDeadProdExchange()).with(DebugRabbitConsts.MAIL_SUCCESS_DEAD_TTL_ROUTING);
    }

    //真正的队列
    @Bean
    public Queue successKillRealQueue() {
        return new Queue(DebugRabbitConsts.MAIL_SUCCESS_DEAD_REAL_QUEUE,true);
    }

    //死信交换机
    @Bean
    public TopicExchange successKillDeadExchange() {
        return new TopicExchange(DebugRabbitConsts.MAIL_SUCCESS_DEAD_EXCHANGE, true, false);
    }

    //死信交换机+死信路由->真正队列 的绑定
    @Bean
    public Binding successKillDeadBinding() {
        return BindingBuilder.bind(successKillRealQueue()).to(successKillDeadExchange()).with(DebugRabbitConsts.MAIL_SUCCESS_DEAD_ROUTING);
    }
}
