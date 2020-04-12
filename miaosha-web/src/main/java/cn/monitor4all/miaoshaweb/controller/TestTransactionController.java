package cn.monitor4all.miaoshaweb.controller;

import cn.monitor4all.miaoshadao.dao.User;
import cn.monitor4all.miaoshaservice.service.transation.ITestServiceA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author xiyou
 */
@RestController
@Api("debug秒杀相关")
@RequestMapping("/debug")
@Slf4j
public class TestTransactionController {


    @Autowired
    private ITestServiceA serviceA;


    @PostMapping("/testTransaction")
    @ApiOperation(value = "测试事务的传播行为", notes = "测试")
    public String createOptimisticOrderAop2() {
        List<User> userList = new ArrayList<>();
        Long time = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            //id自增，只插入名称
            userList.add(new User("小事务插入的数据" + time));
        }
        int success = serviceA.insertUserWithBservice(userList, "1", "1");
        return "测试事务的传播行为" + success;
    }


}
