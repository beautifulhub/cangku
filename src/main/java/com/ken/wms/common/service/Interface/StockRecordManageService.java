package com.ken.wms.common.service.Interface;

import com.ken.wms.domain.StockRecordDTO;
import com.ken.wms.exception.StockRecordManageServiceException;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 进出货开单
 *
 * @author Bea
 */
public interface StockRecordManageService {

    /**
     * 货物进货操作
     *
     * @param supplierID   供应商ID
     * @param goodsNO      货物编号
     * @param goodsName    货物名称
     * @param goodsDetail  货物明细
     * @param repositoryID 进货仓库ID
     * @param personInCharge 进货人员
     * @return 返回一个boolean 值，若值为true表示进货成功，否则表示进货失败
     */
    boolean stockInOperation(Integer supplierID, String goodsNO, String goodsName, String goodsDetail, Integer repositoryID, String personInCharge, String remark) throws StockRecordManageServiceException;

    /**
     * 货物出货操作
     *
     * @param customerID   客户ID
     * @param repositoryID 出货仓库ID
     * @return 返回一个boolean值，若值为true表示出货成功，否则表示出货失败
     */
    boolean stockOutOperation(Integer customerID, String goodsNO, String goodsName, String goodsDetail, Integer repositoryID, String personInCharge, String remark) throws StockRecordManageServiceException;

    /**
     * 查询进出货记录
     *
     * @param repositoryID 仓库ID
     * @param endDateStr   查询记录起始日期
     * @param startDateStr 查询记录结束日期
     * @param searchType   记录查询方式
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectStockRecord(Integer repositoryID, String startDateStr, String endDateStr, String searchType) throws StockRecordManageServiceException;

    /**
     * 分页查询进出货记录
     *
     * @param repositoryID 仓库ID
     * @param endDateStr   查询记录起始日期
     * @param startDateStr 查询记录结束日期
     * @param searchType   记录查询方式
     * @param offset       分页偏移值
     * @param limit        分页大小
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectStockRecord(Integer repositoryID, String startDateStr, String endDateStr, String searchType, int offset, int limit) throws StockRecordManageServiceException;

    /**
     * 导出进出货记录
     *
     * @param storagesRecord 进出货记录List
     * @return excel 文件
     */
    File exportJCHRecord(List<StockRecordDTO> storagesRecord);
}
