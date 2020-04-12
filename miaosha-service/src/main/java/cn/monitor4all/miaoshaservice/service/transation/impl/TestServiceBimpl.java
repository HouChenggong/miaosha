package cn.monitor4all.miaoshaservice.service.transation.impl;

import cn.monitor4all.miaoshadao.dao.User;
import cn.monitor4all.miaoshadao.mapper.UserMapper;
import cn.monitor4all.miaoshaservice.service.transation.ITestServiceB;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiyou
 */

@Slf4j
@Service
public class TestServiceBimpl implements ITestServiceB {
    @Autowired
    private UserMapper userMapper;

    /**
     * 批量插入人员之后，之后再更新当前部门的信息
     *
     * @param userList
     * @param deptId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int insertServiceB(List<User> userList, String deptId) {
        int count = userMapper.batchInsertUserList(userList);
        //更新部门表失败的情况，我们用一个异常来替代,这里肯定发生异常，哈哈
        //java.lang.ArithmeticException: / by zero
        int a = 1 / 0;
        int count2 = userMapper.batchInsertUserList(userList);
        return count;
    }
}
