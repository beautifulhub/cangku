package com.ken.wms.dao;


import com.ken.wms.domain.GoodsType;

import java.util.List;

/**
 * 货物信息映射器
 * @author Ken
 *
 */
public interface GoodsTypeMapper {

	/**
	 * 选择所有的 GoodsType
	 * @return 返回所有的GoodsType
	 */
	List<GoodsType> selectAll();
	
	/**
	 * 选择指定 id 的 GoodsType
	 * @param id 货物的ID
	 * @return 返回执行ID对应的GoodsType
	 */
	GoodsType selectById(Integer id);
	
	/**
	 * 插入一条新的记录到数据库
	 * @param goodsType 货物信息
	 */
	void insert(GoodsType goodsType);
	
	/**
	 * 批量插入新的记录到数据库中
	 * @param goodsType 存放 goodsType 信息的 List
	 */
	void insertBatch(List<GoodsType> goodsType);
	
	/**
	 * 更新 GoodsType 到数据库中
	 * 该 Customer 必须已经存在于数据库中，即已经分配主键，否则将更新失败
	 * @param goodsType 货物信息
	 */
	void update(GoodsType goodsType);
	
	/**
	 * 删除指定 id 的 goodsType
	 * @param id 货物ID
	 */
	void deleteById(Integer id);
	
}
