package com.ken.wms.domain;

import java.util.Date;

/**
 * 下架/上架记录DO
 *
 * @author Ken
 * @since 2017/4/5.
 */
public class LocationRecordDTO {

    /**
     * 记录ID
     */
    private Integer recordID;

    /**
     * 记录的类型（下架/上架）
     */
    private String type;

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
     * 下架或上架数量
     */
    private long goodsNum;

    /**
     * 下架或上架数量
     */
    private String locationNO;

    /**
     * 下架或上架仓库ID
     */
    private Integer repositoryID;

    /**
     * 下架或上架时间
     */
    private Date time;

    /**
     * 下架或上架经手人
     */
    private String personInCharge;

    /**
     * 备注说明
     */
    private String remark;

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

    public String getLocationNO() {
        return locationNO;
    }

    public void setLocationNO(String locationNO) {
        this.locationNO = locationNO;
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
        return "LocationRecordDTO{" +
                "recordID=" + recordID +
                ", type='" + type + '\'' +
                ", goodsNO='" + goodsNO + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsColor='" + goodsColor + '\'' +
                ", goodsSize='" + goodsSize + '\'' +
                ", goodsNum=" + goodsNum +
                ", locationNO=" + locationNO +
                ", repositoryID=" + repositoryID +
                ", time=" + time +
                ", personInCharge='" + personInCharge + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
