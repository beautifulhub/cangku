package com.ken.wms.domain;


import java.util.Date;

/**
 * 出库记录
 *
 * @author Ken
 */
public class StockOutDO {

    /**
     * 出库记录ID
     */
    private Integer id;

    /**
     * 客户ID
     */
    private Integer customerID;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 商品ID
     */
    private Integer goodsID;

    /**
     * 商品编号
     */
    private String goodsNO;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品颜色
     */
    private String goodsColor;

    /**
     * 商品尺码
     */
    private String goodsSize;

    /**
     * 入库数量
     */
    private Long goodsNum;

    /**
     * 出库仓库ID
     */
    private Integer repositoryID;

    /**
     * 出库日期
     */
    private Date time;

    /**
     * 出库经手人
     */
    private String personInCharge;// 经手人

    /**
     * 备注说明
     */
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(Integer goodsID) {
        this.goodsID = goodsID;
    }

    public String getGoodsNO() {
        return goodsNO;
    }

    public void setGoodsNO(String goodsNO) {
        this.goodsNO = goodsNO;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsColor() {
        return goodsColor;
    }

    public void setGoodsColor(String goodsColor) {
        this.goodsColor = goodsColor;
    }

    public String getGoodsSize() {
        return goodsSize;
    }

    public void setGoodsSize(String goodsSize) {
        this.goodsSize = goodsSize;
    }

    public Long getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Long goodsNum) {
        this.goodsNum = goodsNum;
    }

    public Integer getRepositoryID() {
        return repositoryID;
    }

    public void setRepositoryID(Integer repositoryID) {
        this.repositoryID = repositoryID;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(String personInCharge) {
        this.personInCharge = personInCharge;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "StockOutDO{" +
                "id=" + id +
                ", customerID=" + customerID +
                ", customerName='" + customerName + '\'' +
                ", goodsID=" + goodsID +
                ", goodsNO='" + goodsNO + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsColor='" + goodsColor + '\'' +
                ", goodsSize='" + goodsSize + '\'' +
                ", goodsNum=" + goodsNum +
                ", repositoryID=" + repositoryID +
                ", time=" + time +
                ", personInCharge='" + personInCharge + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
