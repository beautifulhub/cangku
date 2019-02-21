package com.ken.wms.dao;

import com.ken.wms.domain.Supplier;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Supplier 映射器
 * @author Bea
 *
 */
public interface SupplierMapper {

	/**
	 * 选择全部的 Supplier 附加所属仓库ID
	 * @return 返回所有的供应商
	 */
	List<Supplier> selectAll(Integer repoID);
	
	/**
	 * 选择指定 id 的 Supplier
	 * @param id 供应商ID
	 * @return 返回指定ID对应的供应商
	 */
	Supplier selectById(Integer id);
	
	/**
	 * 选择指定 supplier name 的 Supplier
	 * @param supplierName 供应商名称
	 * @return 返回supplierName对应的供应商
	 */
	Supplier selectByName(@Param("supplierName")String supplierName,@Param("repoID")Integer repoID);
	
	/**
	 * 选择指定 supplier name 的 Supplier
	 * 与 selectBuName 方法的区别在于本方法将返回相似匹配的结果
	 * @param supplierName 供应商名
	 * @return 返回所有模糊匹配指定supplierName的供应商
	 */
	List<Supplier> selectApproximateByName(@Param("supplierName")String supplierName,@Param("repoID")Integer repoID);

	/**
	 * 选择指定 supplier principal 的 Supplier
	 * 返回相似匹配的结果
	 * @param principal 供应商负责人
	 * @return 返回所有模糊匹配指定principal的供应商
	 */
	List<Supplier> selectApproximateByPrincipal(@Param("principal")String principal,@Param("repoID")Integer repoID);

	/**
	 * 插入 Supplier 到数据库中
	 * 不需要指定 Supplier 的主键，采用的数据库 AI 方式
	 * @param supplier Supplier 实例
	 */
	void insert(Supplier supplier);
	
	/**
	 * 批量插入 Supplier 到数据库中
	 * @param suppliers 存放 Supplier 实例的 Lists
	 */
	void insertBatch(List<Supplier> suppliers);
	
	/**
	 * 更新 Supplier 到数据库
	 * 该 Supplier 必须已经存在于数据库中，即已经分配主键，否则将更新失败
	 * @param supplier Supplier 实例
	 */
	void update(Supplier supplier);
	
	/**
	 * 删除指定 id 的Supplier
	 * @param id 供应商ID
	 */
	void deleteById(Integer id);
	
	/**
	 * 删除指定 supplierName 的 Supplier
	 * @param supplierName 供应商名称
	 */
	void deleteByName(String supplierName);
	
}
