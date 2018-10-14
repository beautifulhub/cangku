package com.ken.wms.dao;

import com.ken.wms.domain.LocationUpDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 上架记录映射器
 *
 * @author Ken
 */
public interface LocationUpMapper {

    /**
     * 选择指定条件的上架记录
     *
     * @param repositoryID 指定的仓库ID
     * @param startDate    记录的起始日期
     * @param endDate      记录的结束日期
     * @return 返回所有符合要求的上架记录
     */
    List<LocationUpDO> selectBySearch(@Param("goodsNO") String goodsNO,@Param("goodsName") String goodsName,
                                      @Param("goodsColor") String goodsColor,@Param("goodsSize") String goodsSize,
                                      @Param("repositoryID") Integer repositoryID,@Param("personID") Integer personID,
                                      @Param("startDate") Date startDate,@Param("endDate") Date endDate);

    /**
     * 添加一条新的上架记录
     *
     * @param locationUpDO 上架记录
     */
    void insert(LocationUpDO locationUpDO);

    /**
     * 批量添加上架记录
     *
     * @param locationUpDOs 上架记录
     */
    void insertBatch(List<LocationUpDO> locationUpDOs);

}
