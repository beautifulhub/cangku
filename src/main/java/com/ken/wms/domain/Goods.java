package com.ken.wms.domain;

/**
 * 货物信息
 * @author Bea
 *
 */
public class Goods {

	private Integer id;// ID标识
	private String no;// 货物编号
	private String name;// 货物名
	private String type;// 货物类型
	private String sizes;// 货物尺码
	private String colors;// 货物颜色
	private String pic;// 货物实物图
	private Double value;// 货物价值
	private String remark;// 货物说明

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSizes() {
		return sizes;
	}

	public void setSizes(String sizes) {
		this.sizes = sizes;
	}

	public String getColors() {
		return colors;
	}

	public void setColors(String colors) {
		this.colors = colors;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "Goods{" +
				"id=" + id +
				", no='" + no + '\'' +
				", name='" + name + '\'' +
				", type='" + type + '\'' +
				", colors='" + colors + '\'' +
				", sizes='" + sizes + '\'' +
				", pic='" + pic + '\'' +
				", value=" + value +
				", remark='" + remark + '\'' +
				'}';
	}
}
