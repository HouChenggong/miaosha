package cn.monitor4all.miaoshadao.mapper;

import java.util.List;

import cn.monitor4all.miaoshadao.dao.StockOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockOrderMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(StockOrder record);

    int insertSelective(StockOrder record);

    StockOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StockOrder record);

    int updateByPrimaryKey(StockOrder record);

    /**
     * 使超时未支付的订单失效
     *
     * @param id
     * @return
     */
    int expireOrder(Integer id);

    /**
     * 查询用户一件商品有几个订单了
     *
     * @param sid
     * @param userId
     * @return
     */
    int selectBySidAndUserId(@Param("sid") Integer sid, @Param("userId") Integer userId);
}