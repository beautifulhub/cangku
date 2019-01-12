package com.ken.wms.dao;

import com.ken.wms.domain.LocationStorage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存信息映射器
 * @author Bea
 *
 */
public interface LocationStorageMapper {

	/**
	 * 根据仓库id查询仓库中所有的出入库库存
	 * @return 返回所有的架子的库存信息
	 */
	List<LocationStorage> selectAllByRepositoryID(@Param("repositoryID") Integer repositoryID);

	/**
	 * 根据架位库存编号查询架位库存信息
	 * @return 返回所有的架子的库存信息
	 */
	LocationStorage selectByID(@Param("storageID") Integer storageID);

	/**
	 * 根据架位ID查询架位库存信息
	 * @return 返回所有的架位的库存信息
	 */
	//List<LocationStorage> selectAllByLocationID(@Param("locationID") Integer locationID);

	/**
	 * 选择货位库存信息
	 * @return 返回所有指定货物ID和仓库ID的库存信息
	 */
	List<LocationStorage> selectBySearch(@Param("locationNO") String locationNO, @Param("goodsNO") String goodsNO, @Param("goodsName") String goodsName,
										 @Param("goodsColor") String goodsColor, @Param("goodsSize") String goodsSize,
                                                   @Param("repositoryID") Integer repositoryID);
	Long selectBySearchToTotalNum(@Param("locationNO") String locationNO, @Param("goodsNO") String goodsNO, @Param("goodsName") String goodsName,
										 @Param("goodsColor") String goodsColor, @Param("goodsSize") String goodsSize,
                                                   @Param("repositoryID") Integer repositoryID);
	/**
	 * 选择货位库存信息
	 * @return 返回所有指定货物ID和仓库ID的库存信息
	 */
	LocationStorage selectByGoodsParam(@Param("goodsID") Integer goodsID,@Param("goodsColor") String goodsColor,
									   @Param("goodsSize") String goodsSize, @Param("locationNO") String locationNO,
									   @Param("repositoryID") Integer repositoryID);

	/**
	 * 选择指定货物ID和仓库ID的库存信息
	 * @param goodsID 货物ID
	 * @param repositoryID 库存ID
	 * @return 返回所有指定货物ID和仓库ID的货位库存信息
	 */
	List<LocationStorage> selectByGoodsIDAndRepositoryID(@Param("goodsID") Integer goodsID,
												 @Param("repositoryID") Integer repositoryID);

	/**
	 * 添加一条
	 *
	 * @param locationStorage 库存信息
	 */
	void insert(LocationStorage locationStorage);

	/**
	 * 批量添加
	 *
	 * @param locationStorageList 库存信息
	 */
	void insertBatch(List<LocationStorage> locationStorageList);

	/**
	 * 更新库存信息
	 * 该库存信息必需已经存在于数据库当中，否则更新无效
	 * @param storageID
	 * @param goodsNum
	 */
	void update(@Param("storageID") Integer storageID, @Param("goodsNum") Long goodsNum );

	/**
	 * 删除对应的货位库存
	 * @param storageID 货位库存ID
	 */
	void delete(@Param("storageID") Integer storageID);

}
