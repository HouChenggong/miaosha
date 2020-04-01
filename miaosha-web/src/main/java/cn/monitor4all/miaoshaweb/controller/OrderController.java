package cn.monitor4all.miaoshaweb.controller;

import cn.monitor4all.miaoshaservice.service.OrderService;
import cn.monitor4all.miaoshaservice.service.StockService;
import cn.monitor4all.miaoshaservice.service.UserService;
import cn.monitor4all.miaoshaweb.limit.AnRateLimiter;
import com.google.common.util.concurrent.RateLimiter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Controller
@Api("秒杀")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private StockService stockService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    //Guava令牌桶：每秒放行10个请求
    RateLimiter rateLimiter = RateLimiter.create(10);

    /**
     * 导致超卖的错误示范
     *
     * @param sid
     * @return
     */
    @GetMapping("/createWrongOrder/{sid}")
    @ResponseBody
    @ApiOperation(value = "导致超卖的错误示范", notes = "项目信息")
    public String createWrongOrder(@PathVariable int sid) {
        int id = 0;
        try {
            id = orderService.createWrongOrder(sid);
            LOGGER.info("创建订单id: [{}]", id);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return String.valueOf(id);
    }

    /**
     * 乐观锁更新库存 + 令牌桶限流
     *
     * @param sid
     * @return
     */
    @GetMapping("/createOptimisticOrder/{sid}")
    @ApiOperation(value = "乐观锁更新库存 + 令牌桶限流", notes = "项目信息")
    @ResponseBody
    public String createOptimisticOrder(@PathVariable int sid) {
        // 阻塞式获取令牌
        LOGGER.info("等待时间" + rateLimiter.acquire());
        // 非阻塞式获取令牌
//        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
//            LOGGER.warn("你被限流了，真不幸，直接返回失败");
//            return "你被限流了，真不幸，直接返回失败";
//        }
        int id;
        try {
            id = orderService.createOptimisticOrder(sid);
            LOGGER.info("购买成功，剩余库存为: [{}]", id);
        } catch (Exception e) {
            LOGGER.error("购买失败：[{}]", e.getMessage());
            return "购买失败，库存不足";
        }
        return String.format("购买成功，剩余库存为：%d", id);
    }

    /**
     * 乐观锁更新库存 + 令牌桶限流
     *
     * @param sid
     * @return
     */
    @GetMapping("/createOptimisticOrderAop10/{sid}")
    @ApiOperation(value = "乐观锁更新库存 + 令牌桶限流AOP注解", notes = "每秒可以有10个成功")
    @ResponseBody
    @AnRateLimiter(permitsPerSecond = 10, timeout = 500, timeunit = TimeUnit.MILLISECONDS, msg = "亲,现在流量过大,请稍后再试.")
    public String createOptimisticOrderAop(@PathVariable int sid) {
        int id;
        try {
            id = orderService.createOptimisticOrder(sid);
            LOGGER.info("购买成功，剩余库存为: [{}]", id);
        } catch (Exception e) {
            LOGGER.error("购买失败：[{}]", e.getMessage());
            return "购买失败，库存不足";
        }
        return String.format("购买成功，剩余库存为：%d", id);
    }


    /**
     * 乐观锁更新库存 + 令牌桶限流
     *
     * @param sid
     * @return
     */
    @GetMapping("/createOptimisticOrderAop100/{sid}")
    @ApiOperation(value = "乐观锁更新库存 + 令牌桶限流AOP注解", notes = "每秒可以有100个成功")
    @ResponseBody
    @AnRateLimiter(permitsPerSecond = 100, timeout = 500, timeunit = TimeUnit.MILLISECONDS, msg = "亲,现在流量过大,请稍后再试.")
    public String createOptimisticOrderAop2(@PathVariable int sid) {
        int id;
        try {
            id = orderService.createOptimisticOrder(sid);
            LOGGER.info("购买成功，剩余库存为: [{}]", id);
        } catch (Exception e) {
            LOGGER.error("购买失败：[{}]", e.getMessage());
            return "购买失败，库存不足";
        }
        return String.format("购买成功，剩余库存为：%d", id);
    }

    /**
     * 悲观锁更新库存：事务for update更新库存
     *
     * @param sid
     * @return
     */
    @ApiOperation(value = "悲观锁更新库存：事务for update更新库存", notes = "项目信息")
    @GetMapping("/createPessimisticOrder/{sid}")
    @ResponseBody
    public String createPessimisticOrder(@PathVariable int sid) {
        int id;
        try {
            id = orderService.createPessimisticOrder(sid);
            LOGGER.info("购买成功，剩余库存为: [{}]", id);
        } catch (Exception e) {
            LOGGER.error("购买失败：[{}]", e.getMessage());
            return "购买失败，库存不足";
        }
        return String.format("购买成功，剩余库存为：%d", id);
    }

    /**
     * 获取验证值
     *
     * @return
     */
    @GetMapping(value = "/getVerifyHash")
    @ApiOperation(value = "获取验证值", notes = "项目信息")
    @ResponseBody
    public String getVerifyHash(@RequestParam(value = "sid") Integer sid,
                                @RequestParam(value = "userId") Integer userId) {
        String hash;
        try {
            hash = userService.getVerifyHash(sid, userId);
        } catch (Exception e) {
            LOGGER.error("获取验证hash失败，原因：[{}]", e.getMessage());
            return "获取验证hash失败";
        }
        return String.format("请求抢购验证hash值为：%s", hash);
    }

    /**
     * 要求验证的抢购接口
     *
     * @param sid
     * @return
     */
    @ApiOperation(value = "要求验证的抢购接口", notes = "项目信息")
    @GetMapping(value = "/createOrderWithVerifiedUrl")
    @ResponseBody
    public String createOrderWithVerifiedUrl(@RequestParam(value = "sid") Integer sid,
                                             @RequestParam(value = "userId") Integer userId,
                                             @RequestParam(value = "verifyHash") String verifyHash) {
        int stockLeft;
        try {
            stockLeft = orderService.createVerifiedOrder(sid, userId, verifyHash);
            LOGGER.info("购买成功，剩余库存为: [{}]", stockLeft);
        } catch (Exception e) {
            LOGGER.error("购买失败：[{}]", e.getMessage());
            return e.getMessage();
        }
        return String.format("购买成功，剩余库存为：%d", stockLeft);
    }

    /**
     * 要求验证的抢购接口 + 单用户限制访问频率
     *
     * @param sid
     * @return
     */
    @ApiOperation(value = "要求验证的抢购接口 + 单用户限制访问频率", notes = "项目信息")
    @GetMapping(value = "/createOrderWithVerifiedUrlAndLimit")
    @ResponseBody
    public String createOrderWithVerifiedUrlAndLimit(@RequestParam(value = "sid") Integer sid,
                                                     @RequestParam(value = "userId") Integer userId,
                                                     @RequestParam(value = "verifyHash") String verifyHash) {
        int stockLeft;
        try {
            int count = userService.addUserCount(userId);
            LOGGER.info("用户截至该次的访问次数为: [{}]", count);
            boolean isBanned = userService.getUserIsBanned(userId);
            if (isBanned) {
                return "购买失败，超过频率限制";
            }
            stockLeft = orderService.createVerifiedOrder(sid, userId, verifyHash);
            LOGGER.info("购买成功，剩余库存为: [{}]", stockLeft);
        } catch (Exception e) {
            LOGGER.error("购买失败：[{}]", e.getMessage());
            return e.getMessage();
        }
        return String.format("购买成功，剩余库存为：%d", stockLeft);
    }

}
