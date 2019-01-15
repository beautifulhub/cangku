package com.ken.wms.common.service.Interface;

import com.ken.wms.domain.LocationDownDO;
import com.ken.wms.domain.LocationRecordDTO;
import com.ken.wms.domain.LocationUpDO;
import com.ken.wms.exception.LocationRecordManageServiceException;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 出入库管理
 *
 * @author Bea
 */
public interface LocationRecordManageService {

    /**
     * 货物入库操作
     *
     * @return 返回一个boolean 值，若值为true表示入库成功，否则表示入库失败
     */
    boolean locationUpOperation(Integer goodsID, String goodsName, String goodsDetail, Integer repositoryID, Integer personID, String remark) throws LocationRecordManageServiceException;

    /**
     * 货物出库操作
     *
     * @return 返回一个boolean值，若值为true表示出库成功，否则表示出库失败
     */
    boolean locationDownOperation(Integer goodsID, String goodsName, String goodsDetail, Integer repositoryID, Integer personID, String remark) throws LocationRecordManageServiceException;

    /**
     * 查询出入库记录
     *
     * @param repositoryID 仓库ID
     * @param endDateStr   查询记录起始日期
     * @param startDateStr 查询记录结束日期
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectLocationRecord(String goodsNo, String goodsName, String goodsColor, String goodsSize, Integer repositoryID, Integer personID, String startDateStr, String endDateStr, String upOrDown) throws LocationRecordManageServiceException;

    /**
     * 分页查询出入库记录
     *
     * @param repositoryID 仓库ID
     * @param endDateStr   查询记录起始日期
     * @param startDateStr 查询记录结束日期
     * @param offset       分页偏移值
     * @param limit        分页大小
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectLocationRecordPage(String goodsNo, String goodsName, String goodsColor, String goodsSize, Integer repositoryID, Integer personID, String startDateStr, String endDateStr, String upOrDown, int offset, int limit) throws LocationRecordManageServiceException;

    /**
     * 导出出入库记录
     *
     * @param locationStoRecord 出入库记录List
     * @return excel 文件
     */
    File exportCRKRecord(List<LocationRecordDTO> locationStoRecord);
}
