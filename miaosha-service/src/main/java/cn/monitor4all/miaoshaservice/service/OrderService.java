package cn.monitor4all.miaoshaservice.service;

public interface OrderService {

    /**
     * 创建订单
     *
     * @param sid 库存ID
     * @return 订单ID
     */
    public int createWrongOrder(int sid);


    /**
     * 创建订单 乐观锁
     *
     * @param sid
     * @return
     * @throws Exception
     */
    public int createOptimisticOrder(int sid);

    /**
     * 创建订单 悲观锁 for update
     *
     * @param sid
     * @return
     * @throws Exception
     */
    public int createPessimisticOrder(int sid);

    public int createVerifiedOrder(Integer sid, Integer userId, String verifyHash) throws Exception;


    /**
     * 创建订单 乐观锁
     *
     * @param sid
     * @return
     * @throws Exception
     */
    public int createOptimisticOrderAndSendMsg(int sid);


    /**
     * redis 分布式锁秒杀
     *
     * @param sid
     * @return
     */
    public int redisKill(int sid, Integer userId);

    /**
     * 查询用户有几个相同商品的订单
     *
     * @param sid
     * @param userId
     * @return
     */
    public int checkOrderBySidAndUserId(int sid, Integer userId);
}
