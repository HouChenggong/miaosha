package cn.monitor4all.miaoshaservice.rabbit.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "xiyou")
public class HelloReceiverTwo {

    @RabbitHandler
    public void process(String hello) {
        System.out.println("xiyouReceiver2  : " + hello);
    }
}