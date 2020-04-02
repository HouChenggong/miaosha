package cn.monitor4all.miaoshaweb.controller;


import cn.monitor4all.miaoshaservice.rabbit.rabbit.CallBackSender;
import cn.monitor4all.miaoshaservice.rabbit.rabbit.HelloSender;
import cn.monitor4all.miaoshaservice.rabbit.rabbit.HelloSenderTwo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RabbitController {

    @Autowired
    private HelloSender helloSender;
    @Autowired
    private HelloSenderTwo helloSenderTwo;
    @Autowired
    private CallBackSender callBackSender;

    /*
        一个生产者和一个消费者
     */
    @GetMapping("/rabbitHello")
    @ResponseBody
    public void hello() {
        helloSender.send();
    }


    /**
     * 单生产者-多消费者
     */
    @GetMapping("/oneToMany")
    @ResponseBody
    public void oneToMany() {
        for (int i = 0; i < 10; i++) {
            helloSender.sendMsg("这是第二个生产者发送的消息:===" + i + "====个");
        }
    }


    /**
     * 多生产者-多消费者
     */
    @GetMapping("/manyToMany")
    @ResponseBody
    public void manyToMany() {
        for (int i = 0; i < 1000; i++) {
            helloSender.sendMsg("---------------:" + i + "    ");
            helloSenderTwo.sendMsg("===============:" + i + "      ");
        }

    }

    /**
     * 实体类传输测试
     */
    @GetMapping("/entityTest")
    @ResponseBody
    public void userTest() {
        helloSender.sendEntity();
    }

    /**
     * topic exchange类型rabbitmq测试
     */
    @GetMapping("/topicTest")
    @ResponseBody
    public void topicTest() {
        helloSender.sendTopic();
    }

    /**
     * fanout exchange类型rabbitmq测试
     */
    @GetMapping("/fanoutTest")
    @ResponseBody
    public void fanoutTest() {
        helloSender.sendFanout();
    }

    @GetMapping("/callback")
    @ResponseBody
    public void callbak() {
        callBackSender.send();
    }
}
