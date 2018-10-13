package com.ken.wms.common.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ken.wms.common.service.Interface.LocationStorageManageService;
import com.ken.wms.common.util.EJConvertor;
import com.ken.wms.common.util.FileUtil;
import com.ken.wms.dao.GoodsMapper;
import com.ken.wms.dao.LocationMapper;
import com.ken.wms.dao.RepositoryMapper;
import com.ken.wms.dao.LocationStorageMapper;
import com.ken.wms.domain.*;
import com.ken.wms.domain.LocationStorage;
import com.ken.wms.exception.LocationStorageManageServiceException;
import com.ken.wms.util.aop.UserOperation;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 货位库存信息管理 service 实现类
 *
 * @author Ken
 */
@Service
public class LocationStorageManageServiceImpl implements LocationStorageManageService {

    @Autowired
    private LocationStorageMapper locationStorageMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RepositoryMapper repositoryMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private EJConvertor ejConvertor;

    /**
     * 返回所有的货位库存记录
     *
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @Override
    public Map<String, Object> selectAllBySearch(String goodsNO,String goodsName,String goodsColor, String goodsSize, Integer repositoryID) throws LocationStorageManageServiceException {
        return selectAllBySearchPage(goodsNO,goodsName,goodsColor, goodsSize, repositoryID, -1, -1);
    }

    /**
     * 分页返回所有的货位库存记录
     *
     * @param offset 分页偏移值
     * @param limit  分页大小
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @Override
    public Map<String, Object> selectAllBySearchPage(String goodsNO,String goodsName,String goodsColor, String goodsSize, Integer repositoryID, int offset, int limit) throws LocationStorageManageServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        List<LocationStorage> locationStorageList;
        long total = 0;
        boolean isPagination = true;

        // validate
        if (offset < 0 || limit < 0)
            isPagination = false;

        // query
        try {
            if (isPagination) {
                PageHelper.offsetPage(offset, limit);
                locationStorageList = locationStorageMapper.selectBySearch(goodsNO,goodsName,goodsColor, goodsSize, repositoryID);
                if (locationStorageList != null) {
                    PageInfo<LocationStorage> pageInfo = new PageInfo<>(locationStorageList);
                    total = pageInfo.getTotal();
                } else
                    locationStorageList = new ArrayList<>();
            } else {
                locationStorageList = locationStorageMapper.selectBySearch(goodsNO,goodsName,goodsColor, goodsSize, repositoryID);
                if (locationStorageList != null)
                    total = locationStorageList.size();
                else
                    locationStorageList = new ArrayList<>();
            }
        } catch (PersistenceException e) {
            throw new LocationStorageManageServiceException(e);
        }

        resultSet.put("data", locationStorageList);
        resultSet.put("total", total);
        return resultSet;
    }

    /**
     * 添加一条货位库存记录
     *
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    @UserOperation(value = "添加货位库存记录")
    @Override
    public boolean addLocationStorage(LocationStorage locationStorage) throws LocationStorageManageServiceException {
        try {
            boolean isAvailable = true;

            // validate
            Goods goods = goodsMapper.selectById(locationStorage.getGoodsID());
            Repository repository = repositoryMapper.selectByID(locationStorage.getRepositoryID());
            if (goods == null)
                isAvailable = false;
            if (repository == null)
                isAvailable = false;

            if (isAvailable) {
                // insert
                locationStorageMapper.insert(locationStorage);
            }
            return isAvailable;
        } catch (PersistenceException e) {
            throw new LocationStorageManageServiceException(e);
        }
    }

    /**
     * 更新一条货位库存记录
     *
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    @UserOperation(value = "修改货位库存记录")
    @Override
    public boolean updateStorageByNum(Integer locationStorageID, long number) throws LocationStorageManageServiceException {
        try {
            boolean isUpdate = false;
            // validate
            LocationStorage LocationStorage = locationStorageMapper.selectByID(locationStorageID);
            if (LocationStorage != null) {
                if (number >= 0) {
                    // update
                    locationStorageMapper.update(locationStorageID,number);
                    isUpdate = true;
                }
            }
            return isUpdate;
        } catch (PersistenceException e) {
            throw new LocationStorageManageServiceException(e);
        }
    }

    /**
     * 删除一条货位库存记录
     *
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    @UserOperation(value = "删除货位库存记录")
    @Override
    public boolean deleteLocationStorage(Integer locationStorageID) throws LocationStorageManageServiceException {
        try {
            boolean isDelete = false;
            // validate
            LocationStorage LocationStorage = locationStorageMapper.selectByID(locationStorageID);
            if (LocationStorage != null) {
                // delete
                locationStorageMapper.delete(locationStorageID);
                isDelete = true;
            }
            return isDelete;
        } catch (PersistenceException e) {
            throw new LocationStorageManageServiceException(e);
        }
    }

    /**
     * 导入货位库存记录
     *
     * @param file 保存有的货位库存记录的文件
     * @return 返回一个Map，其中：key为total代表导入的总记录数，key为available代表有效导入的记录数
     */
    @UserOperation(value = "导入货位库存记录")
    @Override
    public Map<String, Object> importLocationStorage(MultipartFile file) throws LocationStorageManageServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        int total = 0;
        int available = 0;
        String errorTip = "";

        // 从文件中读取
        try {
            List<LocationStorage> locationStorageList = ejConvertor.excelReader(LocationStorage.class, FileUtil.convertMultipartFileToFile(file));
            if (locationStorageList != null) {
                total = locationStorageList.size();
                boolean isAvailable;
                List<LocationStorage> availableList = new ArrayList<>();
                Goods goods;
                Location location;
                Repository repository;
                int i = 1; //除去标题所在的首行
                for (LocationStorage locationStorage : locationStorageList) {
                    i++;
                    if (locationStorage.getGoodsNum() < 0) {
                        errorTip = "第"+i+"行货位的数量应该是大于等于0";
                        break;
                    }
                    goods = goodsMapper.selectByNo(locationStorage.getGoodsNO());
                    if (goods == null) {
                        errorTip = "第"+i+"行货位对应的货物编号不存在";
                        break;
                    }else{
                        if(!goods.getColors().contains(locationStorage.getGoodsColor())){
                            errorTip = "第"+i+"行货位对应的货物颜色不存在";
                            break;
                        }else if(!goods.getSizes().contains(locationStorage.getGoodsSize())){
                                errorTip = "第"+i+"行货位对应的货物尺码不存在";
                                break;
                        }
                    }
                    repository = repositoryMapper.selectByID(locationStorage.getRepositoryID());
                    if (repository == null) {
                        errorTip = "第"+i+"行货位对应的仓库编号不存在";
                        break;
                    }
                    location = locationMapper.selectByNo(locationStorage.getRepositoryID(),locationStorage.getLocationNO());
                    if (location == null) {
                        errorTip = "第"+i+"行货位对应的货位编号不存在";
                        break;
                    }
                    availableList.add(locationStorage);
                }
                // 保存到数据库
                if(i == locationStorageList.size() +1 && i > 1){//所有校验都合格
                    available = availableList.size();
                    locationStorageMapper.insertBatch(availableList);
                }
            }
        } catch (PersistenceException | IOException e) {
            throw new LocationStorageManageServiceException(e);
        }
        resultSet.put("errorTip", errorTip);
        resultSet.put("total", total);
        resultSet.put("available", available);
        return resultSet;
    }

    /**
     * 导出货位库存记录
     *
     * @param locationStorageList 保存有货位库存记录的List
     * @return excel 文件
     */
    @UserOperation(value = "导出货位库存记录")
    @Override
    public File exportLocationStorage(List<LocationStorage> locationStorageList) {
        if (locationStorageList == null)
            return null;
        return ejConvertor.excelWriter(LocationStorage.class, locationStorageList);
    }

    /**
     * 为指定的货物货位库存记录增加指定数目
     *
     * @param goodsID      货物ID
     * @param repositoryID 仓库ID
     * @return 返回一个 boolean 值，若值为true表示数目增加成功，否则表示增加失败
     */
    @Override
    public boolean locationStorageIncrease(Integer goodsID, List<String> goodsStr, Integer repositoryID) throws LocationStorageManageServiceException {

        synchronized (this) {
            // 检查对应的货位库存记录是否存在
            String goodsColor = goodsStr.get(0);
            String goodsSize = goodsStr.get(1);
            String goodsNum = goodsStr.get(2);
            String locationNO = goodsStr.get(3);
            LocationStorage locationStorage = getLocationStorage(goodsID, goodsColor, goodsSize, locationNO,repositoryID);
            if (locationStorage != null) {
                long newStorage = locationStorage.getGoodsNum() + Long.parseLong(goodsNum);
                updateStorageByNum(locationStorage.getStorageID(), newStorage);
            } else {
                LocationStorage locStorage = new LocationStorage();
                locStorage.setGoodsID(goodsID);
                locStorage.setGoodsColor(goodsColor);
                locStorage.setGoodsSize(goodsSize);
                locStorage.setGoodsNum(Long.parseLong(goodsNum));
                locStorage.setLocationNO(locationNO);
                locStorage.setRepositoryID(repositoryID);
                addLocationStorage(locStorage);
            }
        }
        return true;
    }

    /**
     * 为指定的货物货位库存记录减少指定的数目
     *
     * @param goodsID      货物ID
     * @param repositoryID 仓库ID
     * @return 返回一个 boolean 值，若值为 true 表示数目减少成功，否则表示减少失败
     */
    @Override
    public boolean locationStorageDecrease(Integer goodsID, List<String> goodsStr, Integer repositoryID) throws LocationStorageManageServiceException {

        synchronized (this) {
            // 检查对应的货位库存记录是否存在
            String goodsColor = goodsStr.get(0);
            String goodsSize = goodsStr.get(1);
            String goodsNum = goodsStr.get(2);
            String locationNO = goodsStr.get(3);
            LocationStorage locationStorage = getLocationStorage(goodsID, goodsColor, goodsSize, locationNO,repositoryID);
            if (null != locationStorage) {
                // 检查货位库存减少数目的范围是否合理
                long number = Long.parseLong(goodsNum);
                if (number < 0 || locationStorage.getGoodsNum() < number)
                    return false;
                long newStorage = locationStorage.getGoodsNum() - number;
                updateStorageByNum(locationStorage.getStorageID(), newStorage);
                return true;
            } else
                return false;
        }
    }

    /**
     * 获取指定货物ID，（具体到尺码、颜色）仓库ID对应的货位库存记录
     *
     * @return 若存在则返回对应的记录，否则返回null
     */
    private LocationStorage getLocationStorage(Integer goodsID, String goodsColor, String goodsSize, String locationNO, Integer repositoryID) {
        return locationStorageMapper.selectByGoodsParam(goodsID, goodsColor, goodsSize,locationNO, repositoryID);
    }
}
