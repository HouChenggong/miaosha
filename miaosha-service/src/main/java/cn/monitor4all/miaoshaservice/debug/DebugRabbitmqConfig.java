package cn.monitor4all.miaoshaservice.debug;


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


}
