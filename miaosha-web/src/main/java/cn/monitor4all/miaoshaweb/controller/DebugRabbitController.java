package cn.monitor4all.miaoshaweb.controller;

import cn.monitor4all.miaoshaservice.debug.send.DebugRabbitSenderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author xiyou
 */
@RestController
@Api("debug秒杀相关")
@RequestMapping("/debug")
public class DebugRabbitController {


    @Autowired
    private DebugRabbitSenderService senderService;


    @GetMapping("/testSendMq/{sid}")
    @ApiOperation(value = "测试发送消息能不能被消费者接受到", notes = "测试")
    public String createOptimisticOrderAop2(@PathVariable int sid) {
        senderService.sendKillSuccessEmailMsg(sid);
        return "正在测试订单编号" + sid + "发送MQ消息的服务";
    }
}
