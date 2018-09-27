package com.ken.wms.domain;


import java.util.Date;

/**
 * 入库记录
 *
 * @author Ken
 */
public class StockInDO {

    /**
     * 入库记录
     */
    private Integer id;

    /**
     * 供应商ID
     */
    private Integer supplierID;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 商品ID
     */
    private Integer goodsID;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 入库仓库ID
     */
    private Integer repositoryID;

    /**
     * 入库数量
     */
    private long number;

    /**
     * 入库日期
     */
    private Date time;

    /**
     * 入库经手人
     */
    private String personInCharge;

    public Integer getRepositoryID() {
        return repositoryID;
    }

    public void setRepositoryID(Integer repositoryID) {
        this.repositoryID = repositoryID;
    }

    public Integer getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(Integer supplierID) {
        this.supplierID = supplierID;
    }

    public Integer getGoodID() {
        return goodsID;
    }

    public void setGoodID(Integer goodsID) {
        this.goodsID = goodsID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getGoodName() {
        return goodsName;
    }

    public void setGoodName(String goodsName) {
        this.goodsName = goodsName;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
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
        return "StockInDO [id=" + id + ", supplierID=" + supplierID + ", supplierName=" + supplierName + ", goodsID="
                + goodsID + ", goodsName=" + goodsName + ", repositoryID=" + repositoryID + ", number=" + number
                + ", time=" + time + ", personInCharge=" + personInCharge + "]";
    }

}
