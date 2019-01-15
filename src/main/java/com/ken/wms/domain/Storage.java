package com.ken.wms.domain;

/**
 * 仓库盘点
 * 
 * @author Bea
 *
 */
public class Storage {

	private Integer storageID;//盘点ID
	private Integer goodsID;// 货物ID
	private String goodsNO;// 货物编号
	private String goodsName;// 货物名称
	private String goodsColor;// 货物颜色
	private String goodsSize;// 货物尺码
	private Long goodsNum;// 货物盘点数量
	private String goodsType;// 货物类型
	private Double goodsValue;// 货物价值
	private Integer repositoryID;// 仓库ID

	public Integer getStorageID() {
		return storageID;
	}

	public void setStorageID(Integer storageID) {
		this.storageID = storageID;
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

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public Double getGoodsValue() {
		return goodsValue;
	}

	public void setGoodsValue(Double goodsValue) {
		this.goodsValue = goodsValue;
	}

	public Integer getRepositoryID() {
		return repositoryID;
	}

	public void setRepositoryID(Integer repositoryID) {
		this.repositoryID = repositoryID;
	}

	@Override
	public String toString() {
		return "Storage{" +
				"storageID=" + storageID +
				", goodsID=" + goodsID +
				", goodsNO='" + goodsNO + '\'' +
				", goodsName='" + goodsName + '\'' +
				", goodsColor='" + goodsColor + '\'' +
				", goodsSize='" + goodsSize + '\'' +
				", goodsNum=" + goodsNum +
				", goodsType='" + goodsType + '\'' +
				", goodsValue=" + goodsValue +
				", repositoryID=" + repositoryID +
				'}';
	}
}
