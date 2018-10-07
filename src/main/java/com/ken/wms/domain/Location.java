package com.ken.wms.domain;

import java.util.Date;

/**
 * 货位信息
 * @author Ken
 *
 */
public class Location {

	private Integer id;// ID标识
	private String no;// 货位编号
	private Date createTime;// 货位创建时间
	private Date updateTime;// 货位修改时间
	private Integer person;// 货位创建人
	private Integer repoID;// 货位所属仓库

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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getPerson() {
		return person;
	}

	public void setPerson(Integer person) {
		this.person = person;
	}

	public Integer getRepoID() {
		return repoID;
	}

	public void setRepoID(Integer repoID) {
		this.repoID = repoID;
	}

	@Override
	public String toString() {
		return "Location{" +
				"id=" + id +
				", no='" + no + '\'' +
				", createTime=" + createTime +
				", updateTime=" + updateTime +
				", person='" + person + '\'' +
				", repoID=" + repoID +
				'}';
	}
}
