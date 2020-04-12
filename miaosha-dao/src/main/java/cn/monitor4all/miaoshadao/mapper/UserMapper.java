package cn.monitor4all.miaoshadao.mapper;

import cn.monitor4all.miaoshadao.dao.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 批量插入
     *
     * @param userList
     * @return
     */
    int batchInsertUserList(List<User> userList);
}