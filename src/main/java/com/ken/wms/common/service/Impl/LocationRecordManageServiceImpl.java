package com.ken.wms.common.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.ken.wms.common.service.Interface.LocationRecordManageService;
import com.ken.wms.common.service.Interface.LocationStorageManageService;
import com.ken.wms.common.service.Interface.StockRecordManageService;
import com.ken.wms.common.service.Interface.StorageManageService;
import com.ken.wms.common.util.EJConvertor;
import com.ken.wms.dao.*;
import com.ken.wms.domain.*;
import com.ken.wms.exception.LocationRecordManageServiceException;
import com.ken.wms.exception.LocationRecordManageServiceException;
import com.ken.wms.exception.LocationStorageManageServiceException;
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
public class LocationRecordManageServiceImpl implements LocationRecordManageService {

    private static final Splitter SPLIT_COMMA = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final Splitter SPLIT_SEMICOLON = Splitter.on(";").trimResults().omitEmptyStrings();
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RepositoryMapper repositoryMapper;
    @Autowired
    private LocationUpMapper locationUpMapper;
    @Autowired
    private LocationDownMapper locationDownMapper;
    @Autowired
    private LocationStorageManageService locationStorageManageService;
    @Autowired
    private EJConvertor ejConvertor;

    /**
     * 货物入库操作
     *
     * @return 返回一个boolean 值，若值为true表示入库成功，否则表示入库失败
     */
    @UserOperation(value = "货物入库")
    @Override
    public boolean locationUpOperation(Integer goodsID, String goodsName, String goodsDetail, Integer repositoryID, Integer personID, String remark) throws LocationRecordManageServiceException {
        try {
            // 更新库存记录
            boolean isSuccess = false;
            //根据货物编号查询货物ID
            /*Integer goodsID = goodsMapper.selectIDByNO(goodsNO);
            if(goodsID == null ){
                return isSuccess;
            }*/
            //组装批量录入的货物信息
            List<String> goodsSingle = SPLIT_SEMICOLON.splitToList(goodsDetail);
            for(String goodss : goodsSingle){
                List<String> goodsStr = SPLIT_COMMA.splitToList(goodss);
                //保存到货位总数
                isSuccess = locationStorageManageService.locationStorageIncrease(goodsID, goodsStr, repositoryID);
                // 保存入库记录
                if (isSuccess) {
                    LocationUpDO locationUpDO = new LocationUpDO();
                    locationUpDO.setGoodsID(goodsID);
                    locationUpDO.setGoodsColor(goodsStr.get(0));
                    locationUpDO.setGoodsSize(goodsStr.get(1));
                    locationUpDO.setGoodsNum(Long.parseLong(goodsStr.get(2)));
                    locationUpDO.setLocationNO(goodsStr.get(3));
                    locationUpDO.setPersonID(personID);
                    locationUpDO.setUpdateTime(new Date());
                    locationUpDO.setRepositoryID(repositoryID);
                    locationUpDO.setRemark(remark);
                    locationUpMapper.insert(locationUpDO);
                }else{
                    return isSuccess;
                }
            }
            return isSuccess;
        } catch (PersistenceException | LocationStorageManageServiceException e) {
            throw new LocationRecordManageServiceException(e);
        }
    }

    /**
     * 货物出库操作
     *
     * @return 返回一个boolean值，若值为true表示出库成功，否则表示出库失败
     */
    @UserOperation(value = "货物出库")
    @Override
    public boolean locationDownOperation(Integer goodsID, String goodsName, String goodsDetail, Integer repositoryID, Integer personID, String remark) throws LocationRecordManageServiceException {
        try {
            // 更新库存信息
            boolean isSuccess = false;
            //组装批量录入的货物信息
            List<String> goodsSingle = SPLIT_SEMICOLON.splitToList(goodsDetail);
            for(String goodss : goodsSingle){
                List<String> goodsStr = SPLIT_COMMA.splitToList(goodss);
                String goodsColor = goodsStr.get(0);
                String goodsSize = goodsStr.get(1);
                String goodsNum = goodsStr.get(2);
                String locationNO = goodsStr.get(3);
                isSuccess = locationStorageManageService.locationStorageDecrease(goodsID, goodsColor, goodsSize, Long.parseLong(goodsNum), locationNO, repositoryID);
                // 保存出库记录
                if (isSuccess) {
                    LocationDownDO locationDownDO = new LocationDownDO();
                    locationDownDO.setGoodsID(goodsID);
                    locationDownDO.setGoodsColor(goodsStr.get(0));
                    locationDownDO.setGoodsSize(goodsStr.get(1));
                    locationDownDO.setGoodsNum(Long.parseLong(goodsStr.get(2)));
                    locationDownDO.setLocationNO(goodsStr.get(3));
                    locationDownDO.setPersonID(personID);
                    locationDownDO.setRepositoryID(repositoryID);
                    locationDownDO.setUpdateTime(new Date());
                    locationDownDO.setRemark(remark);
                    locationDownMapper.insert(locationDownDO);
                }else{
                    return isSuccess;
                }
            }
            return isSuccess;
        } catch (PersistenceException | LocationStorageManageServiceException e) {
            throw new LocationRecordManageServiceException(e);
        }
    }

    /**
     * 查询出入库记录
     *
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @Override
    public Map<String, Object> selectLocationRecord(String goodsNo, String goodsName, String goodsColor, String goodsSize, Integer repositoryID, Integer personID, String startDateStr, String endDateStr, String upOrDown) throws LocationRecordManageServiceException {
        return selectLocationRecordPage(goodsNo, goodsName, goodsColor, goodsSize, repositoryID, personID, startDateStr, endDateStr, upOrDown, -1, -1);
    }

    /**
     * 分页查询出出入库记录
     *
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> selectLocationRecordPage(String goodsNo, String goodsName, String goodsColor, String goodsSize, Integer repositoryID, Integer personID, String startDateStr, String endDateStr, String upOrDown, int offset, int limit) throws LocationRecordManageServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        long total = 0;

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
            throw new LocationRecordManageServiceException(e);
        }

        // 根据查询模式执行查询
        List<LocationRecordDTO> locationRecordDTOS = new ArrayList<>();
        Map<String, Object> locationUpTemp = new HashMap<String,Object>();
        Map<String, Object> locationDownTemp = new HashMap<String,Object>();
        List<LocationUpDO> locationUpRecordDOS = null;
        List<LocationDownDO> locationDownRecordDOS = null;
        switch (upOrDown) {
            case "searchAll": {
                if (offset < 0 || limit < 0) {
                    locationUpTemp = selectLocationUpRecord(goodsNo,goodsName,goodsColor,goodsSize,repositoryID,personID, startDate, endDate, offset, limit);
                    locationDownTemp = selectLocationDownRecord(goodsNo,goodsName,goodsColor,goodsSize,repositoryID,personID, startDate, endDate, offset, limit);
                    locationUpRecordDOS = (List<LocationUpDO>) locationUpTemp.get("data");
                    locationDownRecordDOS = (List<LocationDownDO>) locationDownTemp.get("data");
                } else {
                    /*int locationUpRecordOffset = offset / 2;
                    int locationDownRecordOffset = locationUpRecordOffset * 2 < offset ? locationUpRecordOffset + 1 : locationUpRecordOffset;
                    int locationUpRecordLimit = limit / 2;
                    int locationDownRecordLimit = locationUpRecordLimit * 2 < limit ? locationUpRecordLimit + 1 : locationUpRecordLimit;
                    locationUpTemp = selectLocationUpRecord(goodsNo,goodsName,goodsColor,goodsSize,repositoryID,personID, startDate, endDate, locationUpRecordOffset, limit);
                    locationDownTemp = selectLocationDownRecord(goodsNo,goodsName,goodsColor,goodsSize,repositoryID, personID,startDate, endDate, locationDownRecordOffset, limit);
                    locationUpRecordDOS = (List<LocationUpDO>) locationUpTemp.get("data");
                    locationDownRecordDOS = (List<LocationDownDO>) locationDownTemp.get("data");
                    int locationUpRecordDosSize = locationUpRecordDOS.size();
                    int locationDownRecordDoSize = locationDownRecordDOS.size();
                    if (locationUpRecordDosSize >= locationUpRecordLimit && locationDownRecordDoSize >= locationDownRecordLimit) {
                        locationUpRecordDOS = locationUpRecordDOS.subList(0, locationUpRecordLimit);
                        locationDownRecordDOS = locationDownRecordDOS.subList(0, locationDownRecordLimit);
                    } else if (locationUpRecordDosSize < locationUpRecordLimit && locationDownRecordDoSize > locationDownRecordLimit) {
                        int appendSize = (locationDownRecordDoSize - locationDownRecordLimit) > (locationUpRecordLimit - locationUpRecordDosSize) ?
                                (locationUpRecordLimit - locationUpRecordDosSize) : (locationDownRecordDoSize - locationDownRecordLimit);
                        locationDownRecordDOS = locationDownRecordDOS.subList(0, locationDownRecordLimit + appendSize);
                    } else if (locationDownRecordDoSize < locationDownRecordLimit && locationUpRecordDosSize > locationUpRecordLimit) {
                        int appendSize = (locationUpRecordDosSize - locationUpRecordLimit) > (locationDownRecordLimit - locationDownRecordDoSize) ?
                                (locationDownRecordLimit - locationDownRecordDoSize) : (locationUpRecordDosSize - locationUpRecordLimit);
                        locationUpRecordDOS = locationUpRecordDOS.subList(0, locationUpRecordLimit + appendSize);
                    }*/
                    //先查询入库的，再查询出库的
                    locationUpTemp = selectLocationUpRecord(goodsNo,goodsName,goodsColor,goodsSize,repositoryID,personID, startDate, endDate, offset, limit);
                    locationUpRecordDOS = (List<LocationUpDO>) locationUpTemp.get("data");
                    if((long) locationUpTemp.get("total") < offset + limit){
                        long needNum = offset + limit - (long) locationUpTemp.get("total");
                        if((int)needNum / limit == 0){
                            locationDownTemp = selectLocationDownRecord(goodsNo,goodsName,goodsColor,goodsSize,repositoryID, personID,startDate, endDate, 0, (int)needNum);
                        }else{
                            locationDownTemp = selectLocationDownRecord(goodsNo,goodsName,goodsColor,goodsSize,repositoryID, personID,startDate, endDate, (int)needNum - limit, limit);
                        }
                        locationDownRecordDOS = (List<LocationDownDO>) locationDownTemp.get("data");
                    }else{
                        locationDownTemp = selectLocationDownRecord(goodsNo,goodsName,goodsColor,goodsSize,repositoryID, personID,startDate, endDate, offset, limit);
                    }
                }
                long locationUpRecordDOSTotal = (long) locationUpTemp.get("total");
                long locationDownRecordDOSTotal = (long) locationDownTemp.get("total");
                total = locationUpRecordDOSTotal + locationDownRecordDOSTotal;
                break;
            }
            case "up": {
                locationUpTemp = selectLocationUpRecord(goodsNo,goodsName,goodsColor,goodsSize,repositoryID, personID,startDate, endDate, offset, limit);
                total = (long) locationUpTemp.get("total");
                locationUpRecordDOS = (List<LocationUpDO>) locationUpTemp.get("data");
                break;
            }
            case "down": {
                locationDownTemp = selectLocationDownRecord(goodsNo,goodsName,goodsColor,goodsSize,repositoryID,personID, startDate, endDate, offset, limit);
                total = (long) locationDownTemp.get("total");
                locationDownRecordDOS = (List<LocationDownDO>) locationDownTemp.get("data");
                break;
            }
            case "none": {
                break;
            }
        }

        if (locationUpRecordDOS != null)
            locationUpRecordDOS.forEach(locationUpDO -> locationRecordDTOS.add(locationUpRecordConvertToStockRecordDTO(locationUpDO)));
        if (locationDownRecordDOS != null)
            locationDownRecordDOS.forEach(locationDownDO -> locationRecordDTOS.add(locationDownDoConvertToStockRecordDTO(locationDownDO)));

        resultSet.put("data", locationRecordDTOS);
        resultSet.put("total", total);
        return resultSet;
    }

    /**
     * 导出出入库记录
     *
     * @param locationStoRecord 出入库记录List
     * @return excel 文件
     */
    @UserOperation(value = "导出出入库记录")
    @Override
    public File exportCRKRecord(List<LocationRecordDTO> locationStoRecord) {
        if (locationStoRecord == null)
            return null;
        return ejConvertor.excelWriter(LocationRecordDTO.class, locationStoRecord);
    }

    public static void main(String[] args) {
        java.util.Scanner scan = new java.util.Scanner(System.in);
        System.out.println("请输入一个数字:"); //提示输入一个数
        int offset = scan.nextInt();
        int locationUpRecordOffset = offset / 2;
        int locationDownRecordOffset = locationUpRecordOffset * 2 < offset ? locationUpRecordOffset + 1 : locationUpRecordOffset;
        System.out.println("locationUpRecordOffset:"+locationUpRecordOffset+"///locationDownRecordOffset:"+locationDownRecordOffset);
        java.util.Scanner scan1 = new java.util.Scanner(System.in);
        System.out.println("请输入一个数字:"); //提示输入一个数
        int limit = scan1.nextInt();
        int locationUpRecordLimit = limit / 2;
        int locationDownRecordLimit = locationUpRecordLimit * 2 < limit ? locationUpRecordLimit + 1 : locationUpRecordLimit;
        System.out.println("locationUpRecordLimit:"+locationUpRecordLimit+"///locationDownRecordLimit:"+locationDownRecordLimit);
    }

    /**
     * 查询入库记录
     *
     * @param repositoryID 入库仓库ID
     * @param startDate    入库记录起始日期
     * @param endDate      入库记录结束日期
     * @param offset       分页偏移值
     * @param limit        分页大小
     * @return 返回所有符合要求的入库记录
     */
    private Map<String, Object> selectLocationUpRecord(String goodsNo, String goodsName, String goodsColor, String goodsSize, Integer repositoryID,Integer personID, Date startDate, Date endDate, int offset, int limit) throws LocationRecordManageServiceException {
        Map<String, Object> result = new HashMap<>();
        List<LocationUpDO> locationUpRecords;
        long locationUpTotal = 0;
        boolean isPagination = true;

        // 检查是否需要分页查询
        if (offset < 0 || limit < 0)
            isPagination = false;

        // 查询记录
        try {
            if (isPagination) {
                PageHelper.offsetPage(offset, limit);
                locationUpRecords = locationUpMapper.selectBySearch(goodsNo,goodsName,goodsColor,goodsSize,repositoryID,personID,startDate,endDate);
                if (locationUpRecords != null)
                    locationUpTotal = new PageInfo<>(locationUpRecords).getTotal();
                else
                    locationUpRecords = new ArrayList<>(10);
            } else {
                locationUpRecords = locationUpMapper.selectBySearch(goodsNo,goodsName,goodsColor,goodsSize,repositoryID,personID,startDate,endDate);
                if (locationUpRecords != null)
                    locationUpTotal = locationUpRecords.size();
                else
                    locationUpRecords = new ArrayList<>(10);
            }
        } catch (PersistenceException e) {
            throw new LocationRecordManageServiceException(e);
        }

        result.put("data", locationUpRecords);
        result.put("total", locationUpTotal);
        return result;
    }

    /**
     * 查询出库记录
     *
     * @param repositoryID 出库仓库ID
     * @param startDate    出库记录起始日期
     * @param endDate      出库记录结束日期
     * @param offset       分页偏移值
     * @param limit        分页大小
     * @return 返回所有符合要求的出库记录
     */
    private Map<String, Object> selectLocationDownRecord(String goodsNo, String goodsName, String goodsColor, String goodsSize,Integer repositoryID,Integer personID, Date startDate, Date endDate, int offset, int limit) throws LocationRecordManageServiceException {
        Map<String, Object> result = new HashMap<>();
        List<LocationDownDO> locationDownRecords;
        long locationDownRecordTotal = 0;
        boolean isPagination = true;

        // 检查是否需要分页
        if (offset < 0 || limit < 0)
            isPagination = false;

        // 查询记录
        try {
            if (isPagination) {
                PageHelper.offsetPage(offset, limit);
                locationDownRecords = locationDownMapper.selectBySearch(goodsNo,goodsName,goodsColor,goodsSize,repositoryID, personID,startDate, endDate);
                if (locationDownRecords != null)
                    locationDownRecordTotal = new PageInfo<>(locationDownRecords).getTotal();
                else
                    locationDownRecords = new ArrayList<>(10);
            } else {
                locationDownRecords = locationDownMapper.selectBySearch(goodsNo,goodsName,goodsColor,goodsSize,repositoryID, personID,startDate, endDate);
                if (locationDownRecords != null)
                    locationDownRecordTotal = locationDownRecords.size();
                else
                    locationDownRecords = new ArrayList<>(10);
            }
        } catch (PersistenceException e) {
            throw new LocationRecordManageServiceException(e);
        }

        result.put("data", locationDownRecords);
        result.put("total", locationDownRecordTotal);
        return result;
    }

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");

    /**
     * 将 LocationUpDO 转换为 LocationRecordDTO
     *
     * @param locationUpDO LocationUpDO 对象
     * @return 返回 LocationRecordDTO 对象
     */
    private LocationRecordDTO locationUpRecordConvertToStockRecordDTO(LocationUpDO locationUpDO) {
        LocationRecordDTO locationRecordDTO = new LocationRecordDTO();
        locationRecordDTO.setRecordID(locationUpDO.getId());
        locationRecordDTO.setGoodsNO(locationUpDO.getGoodsNO());
        locationRecordDTO.setGoodsName(locationUpDO.getGoodsName());
        locationRecordDTO.setGoodsColor(locationUpDO.getGoodsColor());
        locationRecordDTO.setGoodsSize(locationUpDO.getGoodsSize());
        locationRecordDTO.setGoodsNum(locationUpDO.getGoodsNum());
        locationRecordDTO.setLocationNO(locationUpDO.getLocationNO());
        locationRecordDTO.setTime(locationUpDO.getUpdateTime());
        locationRecordDTO.setRepositoryID(locationUpDO.getRepositoryID());
        locationRecordDTO.setPersonInCharge(locationUpDO.getPersonName());
        locationRecordDTO.setRemark(locationUpDO.getRemark());
        locationRecordDTO.setType("入库");
        return locationRecordDTO;
    }

    /**
     * 将 LocationDownDO 转换为 LocationRecordDTO 对象
     *
     * @param locationDownDO LocationDownDO 对象
     * @return 返回 LocationRecordDTO 对象
     */
    private LocationRecordDTO locationDownDoConvertToStockRecordDTO(LocationDownDO locationDownDO) {
        LocationRecordDTO locationRecordDTO = new LocationRecordDTO();
        locationRecordDTO.setRecordID(locationDownDO.getId());
        locationRecordDTO.setGoodsNO(locationDownDO.getGoodsNO());
        locationRecordDTO.setGoodsName(locationDownDO.getGoodsName());
        locationRecordDTO.setGoodsColor(locationDownDO.getGoodsColor());
        locationRecordDTO.setGoodsSize(locationDownDO.getGoodsSize());
        locationRecordDTO.setGoodsNum(locationDownDO.getGoodsNum());
        locationRecordDTO.setLocationNO(locationDownDO.getLocationNO());
        locationRecordDTO.setTime(locationDownDO.getUpdateTime());
        locationRecordDTO.setRepositoryID(locationDownDO.getRepositoryID());
        locationRecordDTO.setPersonInCharge(locationDownDO.getPersonName());
        locationRecordDTO.setRemark(locationDownDO.getRemark());
        locationRecordDTO.setType("出库");
        return locationRecordDTO;
    }


}
