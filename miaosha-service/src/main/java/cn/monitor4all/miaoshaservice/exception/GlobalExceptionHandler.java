package cn.monitor4all.miaoshaservice.exception;

import cn.monitor4all.miaoshadao.response.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xiyou
 * 自定义秒杀的异常
 */
@Slf4j
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(value = KillException.class)
    public ServerResponse handleException(Exception e) {
        log.error(e.getMessage());
        return ServerResponse.createByErrorMessage(e.getMessage());
    }
}
