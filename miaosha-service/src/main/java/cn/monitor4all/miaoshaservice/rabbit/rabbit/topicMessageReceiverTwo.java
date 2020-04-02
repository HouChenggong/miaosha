package cn.monitor4all.miaoshaservice.rabbit.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "topic.message2")
public class topicMessageReceiverTwo {

    @RabbitHandler
    public void process(String msg) {
        System.out.println("topic.messageReceiver2  : " +msg);
    }

}
