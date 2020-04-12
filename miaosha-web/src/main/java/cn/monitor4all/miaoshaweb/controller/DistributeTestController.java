package cn.monitor4all.miaoshaweb.controller;

import cn.monitor4all.miaoshadao.response.ServerResponse;
import cn.monitor4all.miaoshaservice.debug.send.DebugRabbitSenderService;
import cn.monitor4all.miaoshaservice.service.OrderService;
import cn.monitor4all.miaoshaservice.service.distributed.IDistributedOrderService;
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
@Api("测试分布式锁相关内容")
@RequestMapping("/distribute")
@Slf4j
public class DistributeTestController {


    @Autowired
    private IDistributedOrderService distributedOrderService;


    @PostMapping("/testRedisKillAndSendMq")
    @ApiOperation(value = "测试redis分布式锁结合死信队列", notes = "测试")
    public ServerResponse testRedisKillAndSendMq() {
        return distributedOrderService.redisKill(1, RandomUtils.nextInt(1000, 10000));
    }

    @PostMapping("/testRedissionKillAndSendMq")
    @ApiOperation(value = "测试redis分布式锁结合死信队列", notes = "测试")
    public ServerResponse testRedissionKillAndSendMq() {
        return distributedOrderService.redissionKill(1, RandomUtils.nextInt(1000, 10000));
    }
}
