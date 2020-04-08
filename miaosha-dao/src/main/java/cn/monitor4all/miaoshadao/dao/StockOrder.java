package cn.monitor4all.miaoshadao.dao;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xiyou
 * 订单表
 * 因为要用rabbitMQ传输，所以要进行序列化
 */
@Data
public class StockOrder implements Serializable {


    private static final long serialVersionUID = 4227062066637066427L;
    /**
     * 订单id
     */
    private Integer id;

    /**
     * 商品id
     */
    private Integer sid;

    /**
     * 订单名称也是商品名称
     */
    private String name;

    private Integer userId;

    private Date createTime;


}