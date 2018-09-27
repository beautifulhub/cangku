package com.ken.wms.domain;

/**
 * 货物信息
 * @author Ken
 *
 */
public class GoodsType {

	private Integer id;// ID标识
	private String name;// 货物类型名称

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
		this.name = name;
	}

	@Override
	public String toString() {
		return "GoodsType{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
