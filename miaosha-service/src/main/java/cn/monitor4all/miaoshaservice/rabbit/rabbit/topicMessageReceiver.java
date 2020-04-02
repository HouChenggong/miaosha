package cn.monitor4all.miaoshaservice.rabbit.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "topic.message1")
public class topicMessageReceiver {

    @RabbitHandler
    public void process(String msg) {
        System.out.println("topic.messageReceiver1  : " +msg);
    }

}
