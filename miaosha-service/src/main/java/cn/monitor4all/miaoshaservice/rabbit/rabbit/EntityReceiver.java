package cn.monitor4all.miaoshaservice.rabbit.rabbit;


import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "entityQueue")
public class EntityReceiver {
    @RabbitHandler
    public void process(RabbitTest rabbitTest) {
        System.out.println("rabbitTest receive  : " + rabbitTest.getName()+"/"+rabbitTest.getPass());
    }
}
