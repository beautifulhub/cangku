package com.ken.wms.domain;


import java.util.Date;

/**
 * 上架记录
 *
 * @author Ken
 */
public class LocationUpDO {

    /**
     * 上架记录
     */
    private Integer id;

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
     * 上架数量
     */
    private Long goodsNum;

    /**
     * 上架货位
     */
    private String locationNO;

    /**
     * 上架仓库ID
     */
    private Integer repositoryID;

    /**
     * 上架更新日期
     */
    private Date updateTime;

    /**
     * 上架经手人ID
     */
    private Integer personID;

    /**
     * 上架经手人姓名
     */
    private String personName;

    /**
     * 上架备注说明
     */
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getPersonID() {
        return personID;
    }

    public void setPersonID(Integer personID) {
        this.personID = personID;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "LocationUpDO{" +
                "id=" + id +
                ", goodsID=" + goodsID +
                ", goodsNO='" + goodsNO + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsColor='" + goodsColor + '\'' +
                ", goodsSize='" + goodsSize + '\'' +
                ", goodsNum=" + goodsNum +
                ", locationNO='" + locationNO + '\'' +
                ", repositoryID=" + repositoryID +
                ", updateTime=" + updateTime +
                ", personID='" + personID + '\'' +
                ", personName='" + personName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
