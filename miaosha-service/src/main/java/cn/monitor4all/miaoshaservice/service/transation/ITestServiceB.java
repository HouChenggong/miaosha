package cn.monitor4all.miaoshaservice.service.transation;

import cn.monitor4all.miaoshadao.dao.User;

import java.util.List;

/**
 * @author xiyou
 * 测试事务的传播行为B
 */
public interface ITestServiceB {

    /**
     * 批量插入人员之后，之后再更新当前部门的信息
     *
     * @param userList
     * @param deptId
     * @return
     */
    int insertServiceB(List<User> userList,String deptId);


}
