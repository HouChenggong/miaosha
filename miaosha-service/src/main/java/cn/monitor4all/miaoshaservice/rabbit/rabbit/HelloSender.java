package cn.monitor4all.miaoshaservice.rabbit.rabbit;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class HelloSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send() {
        String context = "这是发送的信息 " + "---------------------" + new Date();
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("xiyou", context);
    }

    public void sendMsg(String msg) {
        String sendMsg = msg + new Date();
        System.out.println("Sender2 : " + sendMsg);
        this.rabbitTemplate.convertAndSend("xiyou", sendMsg);
    }

    public void sendEntity() {
        RabbitTest rabbitTest = new RabbitTest();
        rabbitTest.setName("琬琬");
        rabbitTest.setPass("123456987");
        this.rabbitTemplate.convertAndSend("entityQueue", rabbitTest);
    }

    public void sendTopic() {
        String msg1 = "I am topic.mesaage msg======";
        System.out.println("sender1 : " + msg1);
        this.rabbitTemplate.convertAndSend("exchange", "topic.message1", msg1);

        String msg2 = "I am topic.mesaages msg########";
        System.out.println("sender2 : " + msg2);
        this.rabbitTemplate.convertAndSend("exchange", "topic.message2", msg2);
    }

    public void sendFanout() {
        String msgString = "A....指定fanoutExchange，但是队列主题任意输入......";
        System.out.println(msgString);
        String msgString2 = "B....指定fanoutExchange，队列主题选主题中的一个......";
        System.out.println(msgString2);
        String msgString3 = "C....不指定指定fanoutExchange,队列主题选其中一个......";
        System.out.println(msgString3);
        String msgString4 = "D....不指定指定fanoutExchange,队列主题随便写一个......";
        System.out.println(msgString4);
        this.rabbitTemplate.convertAndSend("fanoutExchange", "abcd.ee", msgString);
        this.rabbitTemplate.convertAndSend("fanoutExchange", "fanout.B", msgString2);
        this.rabbitTemplate.convertAndSend("fanout.C", msgString3);
        this.rabbitTemplate.convertAndSend("sdasd.asdh", msgString4);

    }

}
