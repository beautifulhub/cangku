package com.ken.wms.domain;

import java.util.Date;

/**
 * 出库/入库记录DO
 *
 * @author Ken
 * @since 2017/4/5.
 */
public class StockRecordDTO {

    /**
     * 记录ID
     */
    private Integer recordID;

    /**
     * 记录的类型（出库/入库）
     */
    private String type;

    /**
     * 供应商（入库）或客户（出库）名称
     */
    private String supplierOrCustomerName;

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
     * 出库或入库数量
     */
    private long goodsNum;

    /**
     * 出库或入库仓库ID
     */
    private Integer repositoryID;

    /**
     * 出库或入库时间
     */
    private Date time;

    /**
     * 出库或入库经手人
     */
    private String personInCharge;

    public Integer getRecordID() {
        return recordID;
    }

    public void setRecordID(Integer recordID) {
        this.recordID = recordID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSupplierOrCustomerName() {
        return supplierOrCustomerName;
    }

    public void setSupplierOrCustomerName(String supplierOrCustomerName) {
        this.supplierOrCustomerName = supplierOrCustomerName;
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

    public long getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(long goodsNum) {
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

    @Override
    public String toString() {
        return "StockRecordDTO{" +
                "recordID=" + recordID +
                ", type='" + type + '\'' +
                ", supplierOrCustomerName='" + supplierOrCustomerName + '\'' +
                ", goodsNO='" + goodsNO + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsColor='" + goodsColor + '\'' +
                ", goodsSize='" + goodsSize + '\'' +
                ", goodsNum=" + goodsNum +
                ", repositoryID=" + repositoryID +
                ", time='" + time + '\'' +
                ", personInCharge='" + personInCharge + '\'' +
                '}';
    }
}
