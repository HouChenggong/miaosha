package cn.monitor4all.miaoshaservice.rabbit.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "xiyou")
public class HelloReceiver {

    @RabbitHandler
    public void process(String xiyou) {
        System.out.println("xiyouReceiver  : " + xiyou);
    }
}
