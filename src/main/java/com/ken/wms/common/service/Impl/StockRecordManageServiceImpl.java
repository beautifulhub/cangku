package com.ken.wms.common.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.ken.wms.common.service.Interface.StockRecordManageService;
import com.ken.wms.common.service.Interface.StorageManageService;
import com.ken.wms.common.util.EJConvertor;
import com.ken.wms.dao.*;
import com.ken.wms.domain.*;
import com.ken.wms.exception.StockRecordManageServiceException;
import com.ken.wms.exception.StorageManageServiceException;
import com.ken.wms.util.aop.UserOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StockRecordManageServiceImpl implements StockRecordManageService {

    private static final Splitter SPLIT_COMMA = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final Splitter SPLIT_SEMICOLON = Splitter.on(";").trimResults().omitEmptyStrings();
    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RepositoryMapper repositoryMapper;
    @Autowired
    private StorageManageService storageManageService;
    @Autowired
    private StockInMapper stockinMapper;
    @Autowired
    private StockOutMapper stockOutMapper;
    @Autowired
    private EJConvertor ejConvertor;

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
    @UserOperation(value = "进货单")
    @Override
    public boolean stockInOperation(Integer supplierID, String goodsNO, String goodsName, String goodsDetail, Integer repositoryID, String personInCharge, String remark) throws StockRecordManageServiceException {

        // ID对应的记录是否存在
        if (!(supplierValidate(supplierID) && repositoryValidate(repositoryID) && goodsNOValidate(goodsNO,repositoryID) ))
            return false;

        if (personInCharge == null)
            return false;

        // 检查进货数量有效性
        /*if (number < 0)
            return false;*/

        try {
            // 更新库存记录
            boolean isSuccess = false;
            //根据货物编号查询货物ID
            Integer goodsID = goodsMapper.selectIDByNO(goodsNO,repositoryID);
            //组装批量录入的货物信息
            List<String> goodsSingle = SPLIT_SEMICOLON.splitToList(goodsDetail);
            for(String goodss : goodsSingle){
                List<String> goodsStr = SPLIT_COMMA.splitToList(goodss);
                isSuccess = storageManageService.storageIncrease(goodsID, goodsNO, goodsName, goodsStr, repositoryID);
                // 保存进货记录
                if (isSuccess) {
                    StockInDO stockInDO = new StockInDO();
                    stockInDO.setSupplierID(supplierID);
                    stockInDO.setGoodsID(goodsID);
                    stockInDO.setGoodsName(goodsName);
                    stockInDO.setGoodsColor(goodsStr.get(0));
                    stockInDO.setGoodsSize(goodsStr.get(1));
                    stockInDO.setGoodsNum(Long.parseLong(goodsStr.get(2)));
                    stockInDO.setPersonInCharge(personInCharge);
                    stockInDO.setTime(new Date());
                    stockInDO.setRepositoryID(repositoryID);
                    stockInDO.setRemark(remark);
                    stockinMapper.insert(stockInDO);
                }
            }
            return isSuccess;
        } catch (PersistenceException | StorageManageServiceException e) {
            throw new StockRecordManageServiceException(e);
        }
    }

    /**
     * 货物出货操作
     *
     * @param customerID   客户ID
     * @param repositoryID 出货仓库ID
     * @return 返回一个boolean值，若值为true表示出货成功，否则表示出货失败
     */
    @UserOperation(value = "出货单")
    @Override
    public boolean stockOutOperation(Integer customerID, String goodsNO, String goodsName, String goodsDetail, Integer repositoryID, String personInCharge, String remark) throws StockRecordManageServiceException {

        // 检查ID对应的记录是否存在
        if (!(customerValidate(customerID) && repositoryValidate(repositoryID) && goodsNOValidate(goodsNO,repositoryID) ))
            return false;

        // 检查出货数量范围是否有效
        /*if (number < 0)
            return false;*/

        try {
            // 更新库存信息
            boolean isSuccess = false;
            //根据货物编号查询货物ID
            Integer goodsID = goodsMapper.selectIDByNO(goodsNO,repositoryID);
            //组装批量录入的货物信息
            List<String> goodsSingle = SPLIT_SEMICOLON.splitToList(goodsDetail);
            for(String goodss : goodsSingle){
                List<String> goodsStr = SPLIT_COMMA.splitToList(goodss);
                isSuccess = storageManageService.storageDecrease(goodsID, goodsNO, goodsName, goodsStr, repositoryID);
                // 保存出货记录
                if (isSuccess) {
                    StockOutDO stockOutDO = new StockOutDO();
                    stockOutDO.setCustomerID(customerID);
                    stockOutDO.setGoodsID(goodsID);
                    stockOutDO.setGoodsName(goodsName);
                    stockOutDO.setGoodsColor(goodsStr.get(0));
                    stockOutDO.setGoodsSize(goodsStr.get(1));
                    stockOutDO.setGoodsNum(Long.parseLong(goodsStr.get(2)));
                    stockOutDO.setPersonInCharge(personInCharge);
                    stockOutDO.setRepositoryID(repositoryID);
                    stockOutDO.setTime(new Date());
                    stockOutDO.setRemark(remark);
                    stockOutMapper.insert(stockOutDO);
                }
            }
            return isSuccess;
        } catch (PersistenceException | StorageManageServiceException e) {
            throw new StockRecordManageServiceException(e);
        }
    }

    /**
     * 查询进出货记录
     *
     * @param repositoryID 仓库ID
     * @param endDateStr   查询记录起始日期
     * @param startDateStr 查询记录结束日期
     * @param searchType   记录查询方式
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @Override
    public Map<String, Object> selectStockRecord(Integer repositoryID, String startDateStr, String endDateStr, String searchType) throws StockRecordManageServiceException {
        return selectStockRecord(repositoryID, startDateStr, endDateStr, searchType, -1, -1);
    }

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
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> selectStockRecord(Integer repositoryID, String startDateStr, String endDateStr, String searchType, int offset, int limit) throws StockRecordManageServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        long total = 0;

        // 检查传入参数
        if (repositoryID == null || searchType == null)
            throw new StockRecordManageServiceException("exception");

        // 转换 Date 对象
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date endDate = null;
        try {
            if (StringUtils.isNotEmpty(startDateStr))
                startDate = dateFormat.parse(startDateStr+" 00:00:00");
            if (StringUtils.isNotEmpty(endDateStr))
                endDate = dateFormat.parse(endDateStr+" 23:59:59");
        } catch (ParseException e) {
            throw new StockRecordManageServiceException(e);
        }

        // 根据查询模式执行查询
        List<StockRecordDTO> stockRecordDTOS = new ArrayList<>();
        Map<String, Object> stockInTemp;
        Map<String, Object> stockOutTemp;
        List<StockInDO> stockInRecordDOS = null;
        List<StockOutDO> stockOutRecordDOS = null;
        switch (searchType) {
            case "searchAll": {
                if (offset < 0 || limit < 0) { //不分页
                    stockInTemp = selectStockInRecord(repositoryID, startDate, endDate, offset, limit);
                    stockOutTemp = selectStockOutRecord(repositoryID, startDate, endDate, offset, limit);
                    stockInRecordDOS = (List<StockInDO>) stockInTemp.get("data");
                    stockOutRecordDOS = (List<StockOutDO>) stockOutTemp.get("data");
                } else {
                    /*int stockInRecordOffset = offset / 2; //求整数部分
                    int stockOutRecordOffset = stockInRecordOffset * 2 < offset ? stockInRecordOffset + 1 : stockInRecordOffset;//0:0 0; 1:0 1; 2:1 1;
                    int stockInRecordLimit = limit / 2; //求整数部分
                    int stockOutRecordLimit = stockInRecordLimit * 2 < limit ? stockInRecordLimit + 1 : stockInRecordLimit;//5:2 3; 10:5 5;
                    //合理分配分页与条数
                    stockInTemp = selectStockInRecord(repositoryID, startDate, endDate, stockInRecordOffset, limit);
                    stockOutTemp = selectStockOutRecord(repositoryID, startDate, endDate, stockOutRecordOffset, limit);
                    stockInRecordDOS = (List<StockInDO>) stockInTemp.get("data");
                    stockOutRecordDOS = (List<StockOutDO>) stockOutTemp.get("data");
                    int stockInRecordDosSize = stockInRecordDOS.size();
                    int stockOutRecordDoSize = stockOutRecordDOS.size();
                    if (stockInRecordDosSize >= stockInRecordLimit && stockOutRecordDoSize >= stockOutRecordLimit) {
                        stockInRecordDOS = stockInRecordDOS.subList(0, stockInRecordLimit);
                        stockOutRecordDOS = stockOutRecordDOS.subList(0, stockOutRecordLimit);
                    } else if (stockInRecordDosSize < stockInRecordLimit && stockOutRecordDoSize > stockOutRecordLimit) {
                        int appendSize = (stockOutRecordDoSize - stockOutRecordLimit) > (stockInRecordLimit - stockInRecordDosSize) ?
                                (stockInRecordLimit - stockInRecordDosSize) : (stockOutRecordDoSize - stockOutRecordLimit);
                        stockOutRecordDOS = stockOutRecordDOS.subList(0, stockInRecordLimit + appendSize);//左闭右开
                    } else if (stockOutRecordDoSize < stockOutRecordLimit && stockInRecordDosSize > stockInRecordLimit) {
                        int appendSize = (stockInRecordDosSize - stockInRecordLimit) > (stockOutRecordLimit - stockOutRecordDoSize) ?
                                (stockOutRecordLimit - stockOutRecordDoSize) : (stockInRecordDosSize - stockInRecordLimit);
                        stockInRecordDOS = stockInRecordDOS.subList(0, stockInRecordLimit + appendSize);
                    }//以上查询进出货数目，最大程度地满足所需条数。*/
                    //先查询进货的，再查询出货的
                    stockInTemp = selectStockInRecord(repositoryID, startDate, endDate, offset, limit);
                    stockInRecordDOS = (List<StockInDO>) stockInTemp.get("data");
                    if((long) stockInTemp.get("total") < offset + limit){
                        long needNum = offset + limit - (long) stockInTemp.get("total");
                        if((int)needNum / limit == 0){
                            stockOutTemp = selectStockOutRecord(repositoryID, startDate, endDate, 0, (int)needNum);
                        }else{
                            stockOutTemp = selectStockOutRecord(repositoryID, startDate, endDate, (int)needNum - limit, limit);
                        }
                        stockOutRecordDOS = (List<StockOutDO>) stockOutTemp.get("data");
                    }else{
                        stockOutTemp = selectStockOutRecord(repositoryID, startDate, endDate, offset, limit);
                    }
                }
                long stockInRecordDOSTotal = (long) stockInTemp.get("total");
                long stockOutRecordDOSTotal = (long) stockOutTemp.get("total");
                total = stockInRecordDOSTotal + stockOutRecordDOSTotal;
                break;
            }
            case "stockInOnly": {
                stockInTemp = selectStockInRecord(repositoryID, startDate, endDate, offset, limit);
                total = (long) stockInTemp.get("total");
                stockInRecordDOS = (List<StockInDO>) stockInTemp.get("data");
                break;
            }
            case "stockOutOnly": {
                stockOutTemp = selectStockOutRecord(repositoryID, startDate, endDate, offset, limit);
                total = (long) stockOutTemp.get("total");
                stockOutRecordDOS = (List<StockOutDO>) stockOutTemp.get("data");
                break;
            }
            case "none": {
                break;
            }
        }

        if (stockInRecordDOS != null)
            stockInRecordDOS.forEach(stockInDO -> stockRecordDTOS.add(stockInRecordConvertToStockRecordDTO(stockInDO)));
        if (stockOutRecordDOS != null)
            stockOutRecordDOS.forEach(stockOutDO -> stockRecordDTOS.add(stockOutDoConvertToStockRecordDTO(stockOutDO)));

        resultSet.put("data", stockRecordDTOS);
        resultSet.put("total", total);
        return resultSet;
    }

    /**
     * 导出货存记录
     *
     * @param storagesRecord 进出货记录List
     * @return excel 文件
     */
    @UserOperation(value = "导出进出货记录")
    @Override
    public File exportJCHRecord(List<StockRecordDTO> storagesRecord) {
        if (storagesRecord == null)
            return null;
        return ejConvertor.excelWriter(StockRecordDTO.class, storagesRecord);
    }

    /**
     * 查询进货记录
     *
     * @param repositoryID 进货仓库ID
     * @param startDate    进货记录起始日期
     * @param endDate      进货记录结束日期
     * @param offset       分页偏移值
     * @param limit        分页大小
     * @return 返回所有符合要求的进货记录
     */
    private Map<String, Object> selectStockInRecord(Integer repositoryID, Date startDate, Date endDate, int offset, int limit) throws StockRecordManageServiceException {
        Map<String, Object> result = new HashMap<>();
        List<StockInDO> stockInRecords;
        long stockInTotal = 0;
        boolean isPagination = true;

        // 检查是否需要分页查询
        if (offset < 0 || limit < 0)
            isPagination = false;

        // 查询记录
        try {
            if (isPagination) {
                PageHelper.offsetPage(offset, limit);
                stockInRecords = stockinMapper.selectByRepositoryIDAndDate(repositoryID, startDate, endDate);
                if (stockInRecords != null)
                    stockInTotal = new PageInfo<>(stockInRecords).getTotal();
                else
                    stockInRecords = new ArrayList<>(10);
            } else {
                stockInRecords = stockinMapper.selectByRepositoryIDAndDate(repositoryID, startDate, endDate);
                if (stockInRecords != null)
                    stockInTotal = stockInRecords.size();
                else
                    stockInRecords = new ArrayList<>(10);
            }
        } catch (PersistenceException e) {
            throw new StockRecordManageServiceException(e);
        }

        result.put("data", stockInRecords);
        result.put("total", stockInTotal);
        return result;
    }

    /**
     * 查询出货记录
     *
     * @param repositoryID 出货仓库ID
     * @param startDate    出货记录起始日期
     * @param endDate      出货记录结束日期
     * @param offset       分页偏移值
     * @param limit        分页大小
     * @return 返回所有符合要求的出货记录
     */
    private Map<String, Object> selectStockOutRecord(Integer repositoryID, Date startDate, Date endDate, int offset, int limit) throws StockRecordManageServiceException {
        Map<String, Object> result = new HashMap<>();
        List<StockOutDO> stockOutRecords;
        long stockOutRecordTotal = 0;
        boolean isPagination = true;

        // 检查是否需要分页
        if (offset < 0 || limit < 0)
            isPagination = false;

        // 查询记录
        try {
            if (isPagination) {
                PageHelper.offsetPage(offset, limit);
                stockOutRecords = stockOutMapper.selectByRepositoryIDAndDate(repositoryID, startDate, endDate);
                if (stockOutRecords != null)
                    stockOutRecordTotal = new PageInfo<>(stockOutRecords).getTotal();
                else
                    stockOutRecords = new ArrayList<>(10);
            } else {
                stockOutRecords = stockOutMapper.selectByRepositoryIDAndDate(repositoryID, startDate, endDate);
                if (stockOutRecords != null)
                    stockOutRecordTotal = stockOutRecords.size();
                else
                    stockOutRecords = new ArrayList<>(10);
            }
        } catch (PersistenceException e) {
            throw new StockRecordManageServiceException(e);
        }

        result.put("data", stockOutRecords);
        result.put("total", stockOutRecordTotal);
        return result;
    }

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");

    /**
     * 将 StockInDO 转换为 StockRecordDTO
     *
     * @param stockInDO StockInDO 对象
     * @return 返回 StockRecordDTO 对象
     */
    private StockRecordDTO stockInRecordConvertToStockRecordDTO(StockInDO stockInDO) {
        StockRecordDTO stockRecordDTO = new StockRecordDTO();
        stockRecordDTO.setRecordID(stockInDO.getId());
        stockRecordDTO.setSupplierOrCustomerName(stockInDO.getSupplierName());
        stockRecordDTO.setGoodsNO(stockInDO.getGoodsNO());
        stockRecordDTO.setGoodsName(stockInDO.getGoodsName());
        stockRecordDTO.setGoodsColor(stockInDO.getGoodsColor());
        stockRecordDTO.setGoodsSize(stockInDO.getGoodsSize());
        stockRecordDTO.setGoodsNum(stockInDO.getGoodsNum());
        stockRecordDTO.setTime(stockInDO.getTime());
        stockRecordDTO.setRepositoryID(stockInDO.getRepositoryID());
        stockRecordDTO.setPersonInCharge(stockInDO.getPersonInCharge());
        stockRecordDTO.setRemark(stockInDO.getRemark());
        stockRecordDTO.setType("进货");
        return stockRecordDTO;
    }

    /**
     * 将 StockOutDO 转换为 StockRecordDTO 对象
     *
     * @param stockOutDO StockOutDO 对象
     * @return 返回 StockRecordDTO 对象
     */
    private StockRecordDTO stockOutDoConvertToStockRecordDTO(StockOutDO stockOutDO) {
        StockRecordDTO stockRecordDTO = new StockRecordDTO();
        stockRecordDTO.setRecordID(stockOutDO.getId());
        stockRecordDTO.setSupplierOrCustomerName(stockOutDO.getCustomerName());
        stockRecordDTO.setGoodsNO(stockOutDO.getGoodsNO());
        stockRecordDTO.setGoodsName(stockOutDO.getGoodsName());
        stockRecordDTO.setGoodsColor(stockOutDO.getGoodsColor());
        stockRecordDTO.setGoodsSize(stockOutDO.getGoodsSize());
        stockRecordDTO.setGoodsNum(stockOutDO.getGoodsNum());
        stockRecordDTO.setTime(stockOutDO.getTime());
        stockRecordDTO.setRepositoryID(stockOutDO.getRepositoryID());
        stockRecordDTO.setPersonInCharge(stockOutDO.getPersonInCharge());
        stockRecordDTO.setRemark(stockOutDO.getRemark());
        stockRecordDTO.setType("出货");
        return stockRecordDTO;
    }


    /**
     * 检查货物ID对应的记录是否存在
     *
     * @param goodsID 货物ID
     * @return 若存在则返回true，否则返回false
     */
    private boolean goodsValidate(Integer goodsID) throws StockRecordManageServiceException {
        try {
            Goods goods = goodsMapper.selectById(goodsID);
            return goods != null;
        } catch (PersistenceException e) {
            throw new StockRecordManageServiceException(e);
        }
    }

    /**
     * 检查货物编号对应的记录是否存在
     *
     * @param goodsNO 货物编号
     * @return 若存在则返回true，否则返回false
     */
    private boolean goodsNOValidate(String goodsNO, Integer repositoryID) throws StockRecordManageServiceException {
        try {
            Goods goods = goodsMapper.selectByNo(goodsNO,repositoryID);
            return goods != null;
        } catch (PersistenceException e) {
            throw new StockRecordManageServiceException(e);
        }
    }

    /**
     * 检查仓库ID对应的记录是否存在
     *
     * @param repositoryID 仓库ID
     * @return 若存在则返回true，否则返回false
     */
    private boolean repositoryValidate(Integer repositoryID) throws StockRecordManageServiceException {
        try {
            Repository repository = repositoryMapper.selectByID(repositoryID);
            return repository != null;
        } catch (PersistenceException e) {
            throw new StockRecordManageServiceException(e);
        }
    }

    /**
     * 检查供应商ID对应的记录是否存在
     *
     * @param supplierID 供应商ID
     * @return 若存在则返回true，否则返回false
     */
    private boolean supplierValidate(Integer supplierID) throws StockRecordManageServiceException {
        try {
            Supplier supplier = supplierMapper.selectById(supplierID);
            return supplier != null;
        } catch (PersistenceException e) {
            throw new StockRecordManageServiceException(e);
        }
    }

    /**
     * 检查客户ID对应的记录是否存在
     *
     * @param cumtomerID 客户ID
     * @return 若存在则返回true，否则返回false
     */
    private boolean customerValidate(Integer cumtomerID) throws StockRecordManageServiceException {
        try {
            Customer customer = customerMapper.selectById(cumtomerID);
            return customer != null;
        } catch (PersistenceException e) {
            throw new StockRecordManageServiceException(e);
        }
    }

}
