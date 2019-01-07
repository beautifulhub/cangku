package com.ken.wms.dao;


import com.ken.wms.domain.Location;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 货位信息映射器
 * @author Bea
 *
 */
public interface LocationMapper {

	/**
	 * 选择所有的 Location
	 * @return 返回所有的Location
	 */
	List<Location> selectAll();

	/**
	 * 选择所有的 Location
	 * @return 返回所有的Location
	 */
	List<Location> selectByRepoID(Integer repoID);

	/**
	 * 选择指定 id 的 Location
	 * @param id 货位的ID
	 * @return 返回执行ID对应的Location
	 */
	Location selectById(Integer id);

	/**
	 * 选择指定 no 的 Location
	 * @param no 货位的编号
	 * @return 返回执行NO对应的Location
	 */
	Location selectByNo(@Param("repoID")Integer repoID, @Param("no") String no);

	/**
	 * 选择指定 no 的 Location
	 * @param no 货位的编号
	 * @return 返回执行NO对应的Location
	 */
	List<Location> selectByLikeNo(@Param("repoID")Integer repoID, @Param("no") String no);

	/**
	 * 插入一条新的记录到数据库
	 * @param Location 货位信息
	 */
	void insert(Location Location);
	
	/**
	 * 批量插入新的记录到数据库中
	 * @param Location 存放 Location 信息的 List
	 */
	void insertBatch(List<Location> Location);
	
	/**
	 * 更新 Location 到数据库中
	 * @param Location 货位信息
	 */
	void update(Location Location);
	
	/**
	 * 删除指定 id 的 Location
	 * @param id 货位ID
	 */
	void deleteById(Integer id);
	
}
