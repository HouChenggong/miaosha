package cn.monitor4all.miaoshaservice.debug.mail;/**
 * Created by Administrator on 2019/6/22.
 */

import cn.monitor4all.miaoshadao.dao.MailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;


/**
 * 异步通用的发送邮件服务
 *
 * @Author:debug (SteadyJack)
 * @Date: 2019/6/22 10:09
 **/
@Service
@EnableAsync
@Slf4j
public class DebugMailService {


    /**
     * 发送简单文本文件
     */
    @Async
    public void sendSimpleEmail(final MailDto dto) {
        try {
            System.out.println("假装发送了邮件、短信" + dto.toString());
            //这里执行具体的邮件或者短信发送方法，这里不再写邮件发送的接口
            log.info("发送邮件、短信成功!" + dto.toString());
        } catch (Exception e) {
            log.error("发生邮件、短信异常： ", e.fillInStackTrace());
        }
    }


}































