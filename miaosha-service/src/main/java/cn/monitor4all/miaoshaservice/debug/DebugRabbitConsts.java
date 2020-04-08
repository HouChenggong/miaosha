package cn.monitor4all.miaoshaservice.debug;

/**
 * @author xiyou
 */
public class DebugRabbitConsts {
    /**
     * 秒杀成功的队列名称
     */
    public static final String MAIL_SUCCESS_QUEUE = "mailSuccessQueue";
    /**
     * 秒杀成功的路由
     */
    public static final String MAIL_SUCCESS_ROUTING = "mailSuccessRoutingKey";


    /**
     * 秒杀成功的交换机
     */
    public static final String MAIL_SUCCESS_EXCHANGE = "mailSuccessExchange";


    /**
     * 秒杀成功的死信队列
     */
    public static final String MAIL_SUCCESS_DEAD_QUEUE = "DeadQueue";


    /**
     * 秒杀成功的死信交换机
     */
    public static final String MAIL_SUCCESS_DEAD_EXCHANGE = "DeadExchange";


    /**
     * 秒杀成功的死信路由
     */
    public static final String MAIL_SUCCESS_DEAD_ROUTING = "DeadRouting";


    /**
     * 秒杀成功的死信队列-真正的队列
     */
    public static final String MAIL_SUCCESS_DEAD_REAL_QUEUE = "DeadRealQueue";


    /**
     * 秒杀成功的死信交换机-真正的队列
     */
    public static final String MAIL_SUCCESS_DEAD_TTL_EXCHANGE = "DeadTtlExchange";


    /**
     * 秒杀成功的死信路由-真正的队列
     */
    public static final String MAIL_SUCCESS_DEAD_TTL_ROUTING = "DeadTtlRouting";

    /**
     * 用户可以多长时间不支付
     */
    public static final String  TTL_TIME = "30000";
}
