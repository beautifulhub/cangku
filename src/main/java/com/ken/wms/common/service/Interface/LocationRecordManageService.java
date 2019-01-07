package com.ken.wms.common.service.Interface;

import com.ken.wms.domain.LocationDownDO;
import com.ken.wms.domain.LocationUpDO;
import com.ken.wms.exception.LocationRecordManageServiceException;

import java.util.List;
import java.util.Map;

/**
 * 上下架管理
 *
 * @author Bea
 */
public interface LocationRecordManageService {

    /**
     * 货物上架操作
     *
     * @return 返回一个boolean 值，若值为true表示上架成功，否则表示上架失败
     */
    boolean locationUpOperation(String goodsNO, String goodsName, String goodsDetail, Integer repositoryID, Integer personID, String remark) throws LocationRecordManageServiceException;

    /**
     * 货物下架操作
     *
     * @return 返回一个boolean值，若值为true表示下架成功，否则表示下架失败
     */
    boolean locationDownOperation(String goodsNO, String goodsName, String goodsDetail, Integer repositoryID, Integer personID, String remark) throws LocationRecordManageServiceException;

    /**
     * 查询上下架记录
     *
     * @param repositoryID 仓库ID
     * @param endDateStr   查询记录起始日期
     * @param startDateStr 查询记录结束日期
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectLocationRecord(String goodsNo, String goodsName, String goodsColor, String goodsSize, Integer repositoryID, Integer personID, String startDateStr, String endDateStr, String upOrDown) throws LocationRecordManageServiceException;

    /**
     * 分页查询上下架记录
     *
     * @param repositoryID 仓库ID
     * @param endDateStr   查询记录起始日期
     * @param startDateStr 查询记录结束日期
     * @param offset       分页偏移值
     * @param limit        分页大小
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectLocationRecordPage(String goodsNo, String goodsName, String goodsColor, String goodsSize, Integer repositoryID, Integer personID, String startDateStr, String endDateStr, String upOrDown, int offset, int limit) throws LocationRecordManageServiceException;
}
