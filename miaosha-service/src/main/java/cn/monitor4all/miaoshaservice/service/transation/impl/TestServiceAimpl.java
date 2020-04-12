package cn.monitor4all.miaoshaservice.service.transation.impl;

import cn.monitor4all.miaoshadao.dao.User;
import cn.monitor4all.miaoshadao.mapper.UserMapper;
import cn.monitor4all.miaoshaservice.service.transation.ITestServiceA;
import cn.monitor4all.miaoshaservice.service.transation.ITestServiceB;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xiyou
 */
@Slf4j
@Service
public class TestServiceAimpl implements ITestServiceA {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ITestServiceB testServiceB;

    /**
     * 批量插入用户，之后更新整个公司的人员总数
     *
     * @param userList
     * @param companyId
     * @param deptId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertUserWithBservice(List<User> userList, String companyId, String deptId) {
        User user = new User();
        user.setUserName("大的方法在小方法执行前插入的数据");
        int num = userMapper.insert(user);
//        try {
            int total = testServiceB.insertServiceB(userList, deptId);
//        }catch (Exception e){
//            System.out.println("内部事务异常");
//        }


        User user2 = new User();
        user2.setUserName("大的方法在小方法执行后。。。。。插入的数据");
        int num2 = userMapper.insert(user2);
        return 0;
    }
}
