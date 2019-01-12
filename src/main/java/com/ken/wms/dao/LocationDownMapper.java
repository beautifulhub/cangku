package com.ken.wms.dao;

import com.ken.wms.domain.LocationDownDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 出库记录映射器
 *
 * @author Bea
 */
public interface LocationDownMapper {

    /**
     * 选择指定条件的出库记录
     *
     * @param repositoryID 指定的仓库ID
     * @param startDate    记录的起始日期
     * @param endDate      记录的结束日期
     * @return 返回所有符合要求的出库记录
     */
    List<LocationDownDO> selectBySearch(@Param("goodsNO") String goodsNO, @Param("goodsName") String goodsName,
                                      @Param("goodsColor") String goodsColor, @Param("goodsSize") String goodsSize,
                                      @Param("repositoryID") Integer repositoryID,@Param("personID") Integer personID,
                                      @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 添加一条新的出库记录
     *
     * @param LocationDownDO 出库记录
     */
    void insert(LocationDownDO LocationDownDO);

    /**
     * 批量添加出库记录
     *
     * @param LocationDownDOs 出库记录
     */
    void insertBatch(List<LocationDownDO> LocationDownDOs);

}
