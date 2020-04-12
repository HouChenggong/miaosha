package cn.monitor4all.miaoshaservice.exception;

/**
 * @author xiyou
 * 自定义秒杀的异常
 */
public class KillException extends RuntimeException {


    public KillException(String message) {
        super(message);
    }
}
