package cn.monitor4all.miaoshaservice.config;/**
 * Created by Administrator on 2019/7/2.
 */

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * redisson通用化配置
 *
 * @Author:debug (xiyou)
 * @Date: 2019/7/2 10:57
 **/
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        if (!StringUtils.isBlank(password)) {
            config.useSingleServer()
                    .setAddress("redis://"+host + ":" + port)
                    .setPassword(password);
        } else {
            config.useSingleServer()
                    .setAddress("redis://"+host + ":" + port);
        }
        RedissonClient client = Redisson.create(config);
        return client;
    }
}