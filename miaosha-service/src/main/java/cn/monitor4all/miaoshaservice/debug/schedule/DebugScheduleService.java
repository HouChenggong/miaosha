package cn.monitor4all.miaoshaservice.debug.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author xiyou
 * 定时任务service
 */
@Service
@Slf4j
@EnableScheduling
public class DebugScheduleService {


    @Scheduled(cron = "0/10 * * * * ?")
    public void scheduleExpire() {
        log.info("定时任务处理失效订单");

    }
    @Scheduled(cron = "0/10 * * * * ?")
    public void scheduleExpireV2() {
        log.info("V2定时任务处理失效订单");

    }
}
