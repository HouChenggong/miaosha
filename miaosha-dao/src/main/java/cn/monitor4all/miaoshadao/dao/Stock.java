package cn.monitor4all.miaoshadao.dao;

/**
 * @author xiyou
 * 商品库存表
 */
public class Stock {
    /**
     * 这里为了方便直接用自增id演示
     */
    private Integer id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 总共有多少
     */
    private Integer count;

    /**
     * 已经卖了多少
     */
    private Integer sale;

    /**
     * MVCC校验版本
     */
    private Integer version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", count=").append(count);
        sb.append(", sale=").append(sale);
        sb.append(", version=").append(version);
        sb.append("]");
        return sb.toString();
    }
}