package cn.monitor4all.miaoshaweb.controller;

import cn.monitor4all.miaoshadao.dao.StockOrder;
import cn.monitor4all.miaoshaservice.debug.send.DebugRabbitSenderService;
import cn.monitor4all.miaoshaservice.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author xiyou
 */
@RestController
@Api("debug秒杀相关")
@RequestMapping("/debug")
@Slf4j
public class DebugRabbitController {


    @Autowired
    private DebugRabbitSenderService senderService;


    @Autowired
    private OrderService orderService;


    @GetMapping("/testSendMq/{sid}")
    @ApiOperation(value = "测试发送消息能不能被消费者接受到", notes = "测试")
    public String createOptimisticOrderAop2(@PathVariable int sid) {
        senderService.sendKillSuccessEmailMsg(sid);
        return "正在测试订单编号" + sid + "发送MQ消息的服务";
    }


    @PostMapping("/testRedisKillAndSendMq")
    @ApiOperation(value = "测试redis分布式锁结合死信队列", notes = "测试")
    public String createOptimisticOrderAop2() {
        orderService.redisKill(1, RandomUtils.nextInt(1000, 10000));
        return "正在测试订单编号" + "发送MQ消息的服务";
    }


    /**
     * 测试用户秒杀结束后，进入死信队列
     *
     * @param sid
     * @return
     */
    @GetMapping("/testDead/{sid}")
    @ApiOperation(value = "测试用户秒杀结束后，进入死信队列", notes = "死信队列")
    @ResponseBody
    public String createOptimisticOrder(@PathVariable int sid) {

        int id;
        try {
            id = orderService.createOptimisticOrderAndSendMsg(sid);
            log.info("购买成功，剩余库存为: [{}]", id);
        } catch (Exception e) {
            log.error("购买失败：[{}]", e.getMessage());
            return "购买失败，库存不足";
        }
        return String.format("购买成功，剩余库存为：%d", id);
    }
}
