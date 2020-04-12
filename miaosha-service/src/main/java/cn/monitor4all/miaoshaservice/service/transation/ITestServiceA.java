package cn.monitor4all.miaoshaservice.service.transation;

import cn.monitor4all.miaoshadao.dao.User;

import java.util.List;

/**
 * @author xiyou
 * 测试事务的传播行为A
 */
public interface ITestServiceA {

    /**
     * 批量插入用户，之后更新整个部门的人员总数，再更新公司的人员总数
     *
     * @param userList
     * @param companyId
     * @param deptId
     * @return
     */
    int insertUserWithBservice(List<User> userList, String companyId, String deptId);
}
