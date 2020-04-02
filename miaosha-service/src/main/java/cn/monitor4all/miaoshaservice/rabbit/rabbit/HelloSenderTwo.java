package cn.monitor4all.miaoshaservice.rabbit.rabbit;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class HelloSenderTwo {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send() {
        String context = "这是发送的信息 " + "---------------------" + new Date();
        System.out.println("Sender———————— 生产者2_: " + context);
        this.rabbitTemplate.convertAndSend("xiyou", context);
    }

    public void sendMsg(String msg) {
        String sendMsg = msg + new Date();
        System.out.println("Sender2__________生产者2 : " + sendMsg);
        this.rabbitTemplate.convertAndSend("xiyou", sendMsg);
    }

}
