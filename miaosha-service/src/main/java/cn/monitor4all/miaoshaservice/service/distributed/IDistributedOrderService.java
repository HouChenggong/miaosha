package cn.monitor4all.miaoshaservice.service.distributed;

import cn.monitor4all.miaoshadao.response.ServerResponse;

/**
 * @author xiyou
 * 分布式秒杀订单接口的实现
 */
public interface IDistributedOrderService {


    /**
     * 用redis秒杀订单
     *
     * @param sid
     * @param userId
     * @return
     */
    ServerResponse redisKill(Integer sid, Integer userId);




    /**
     * 用redission秒杀订单
     *
     * @param sid
     * @param userId
     * @return
     */
    ServerResponse redissionKill(Integer sid, Integer userId);
}
