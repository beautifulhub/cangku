package com.ken.wms.dao;

import com.ken.wms.domain.LocationInDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 上架记录映射器
 *
 * @author Ken
 */
public interface LocationInMapper {

    /**
     * 选择全部的上架记录
     *
     * @return 返回全部的上架记录
     */
    List<LocationInDO> selectAll();

    /**
     * 选择指定上架记录的ID的上架记录
     *
     * @param id 上架记录ID
     * @return 返回指定ID的上架记录
     */
    LocationInDO selectByID(Integer id);

    /**
     * 选择指定货物ID相关的上架记录
     *
     * @param goodsID 指定的货物ID
     * @return 返回指定货物相关的上架记录
     */
    List<LocationInDO> selectByGoodsID(Integer goodsID);

    /**
     * 选择指定仓库ID相关的上架记录
     *
     * @param repositoryID 指定的仓库ID
     * @return 返回指定仓库相关的上架记录
     */
    List<LocationInDO> selectByRepositoryID(Integer repositoryID);

    /**
     * 选择指定仓库ID以及指定日期范围内的上架记录
     *
     * @param repositoryID 指定的仓库ID
     * @param startDate    记录的起始日期
     * @param endDate      记录的结束日期
     * @return 返回所有符合要求的上架记录
     */
    List<LocationInDO> selectByRepositoryIDAndDate(@Param("repositoryID") Integer repositoryID,
                                                @Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate);

    /**
     * 添加一条新的上架记录
     *
     * @param locationInDO 上架记录
     */
    void insert(LocationInDO locationInDO);

    /**
     * 更新上架记录
     *
     * @param locationInDO 上架记录
     */
    void update(LocationInDO locationInDO);

    /**
     * 删除指定ID的上架记录
     *
     * @param id 指定删除上架记录的ID
     */
    void deleteByID(Integer id);
}
