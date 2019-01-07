package com.ken.wms.common.service.Impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.ken.wms.common.service.Interface.LocationManageService;
import com.ken.wms.common.util.EJConvertor;
import com.ken.wms.common.util.FileUtil;
import com.ken.wms.dao.*;
import com.ken.wms.domain.*;
import com.ken.wms.exception.LocationManageServiceException;
import com.ken.wms.util.aop.UserOperation;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 货位信息管理Service 实现类
 *
 * @author Bea
 */
@Service
public class LocationManageServiceImpl implements LocationManageService {

    private static final Splitter SPLIT_COMMA = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final Splitter SPLIT_SEMICOLON = Splitter.on(";").trimResults().omitEmptyStrings();

    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private LocationStorageMapper locationStorageMapper;
    @Autowired
    private RepositoryMapper repositoryMapper;
    @Autowired
    private RepositoryAdminMapper repositoryAdminMapper;
    @Autowired
    private EJConvertor ejConvertor;

    /**
     * 返回指定location ID 的货位记录
     *
     * @param locationId 货位ID
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @Override
    public Map<String, Object> selectById(Integer locationId) throws LocationManageServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        List<Location> locationList = new ArrayList<>();
        long total = 0;

        // 查询
        Location location;
        try {
            location = locationMapper.selectById(locationId);
        } catch (PersistenceException e) {
            throw new LocationManageServiceException(e);
        }

        if (location != null) {
            locationList.add(location);
            total = 1;
        }

        resultSet.put("data", locationList);
        resultSet.put("total", total);
        return resultSet;
    }

    /**
     * 返回指定location NO 的货位记录
     *
     * @param locationNo 货位ID
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @Override
    public Map<String, Object> selectByNo(Integer repoID, String locationNo) throws LocationManageServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        Location location ;
        long total = 0;

        // 查询
        try {
            location = locationMapper.selectByNo(repoID, locationNo);
        } catch (PersistenceException e) {
            throw new LocationManageServiceException(e);
        }

        resultSet.put("data", location);
        resultSet.put("total", total);
        return resultSet;
    }
    /**
     * 返回指定location NO模糊 的货位记录
     *
     * @param locationNo 货位ID
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @Override
    public Map<String, Object> selectByLikeNo(Integer repoID, String locationNo) throws LocationManageServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        List<Location> locationList = new ArrayList<>();
        long total = 0;

        // 查询
        try {
            locationList = locationMapper.selectByLikeNo(repoID, locationNo);
        } catch (PersistenceException e) {
            throw new LocationManageServiceException(e);
        }

        resultSet.put("data", locationList);
        resultSet.put("total", total);
        return resultSet;
    }

    /**
     * 分页查询货位记录
     *
     * @param offset 分页的偏移值
     * @param limit  分页的大小
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @Override
    public Map<String, Object> selectAll(int offset, int limit) throws LocationManageServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        List<Location> locationList;
        long total = 0;
        boolean isPagination = true;

        // validate
        if (offset < 0 || limit < 0)
            isPagination = false;

        // query
        try {
            if (isPagination) {
                PageHelper.offsetPage(offset, limit);
                locationList = locationMapper.selectAll();
                if (locationList != null) {
                    PageInfo<Location> pageInfo = new PageInfo<>(locationList);
                    total = pageInfo.getTotal();
                } else
                    locationList = new ArrayList<>();
            } else {
                locationList = locationMapper.selectAll();
                if (locationList != null)
                    total = locationList.size();
                else
                    locationList = new ArrayList<>();
            }
        } catch (PersistenceException e) {
            throw new LocationManageServiceException(e);
        }

        resultSet.put("data", locationList);
        resultSet.put("total", total);
        return resultSet;
    }

    /**
     * 分页查询货位记录
     *
     * @param offset 分页的偏移值
     * @param limit  分页的大小
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @Override
    public Map<String, Object> selectAllByRepoID(int repoID, int offset, int limit) throws LocationManageServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        List<Location> locationList;
        long total = 0;
        boolean isPagination = true;

        // validate
        if (offset < 0 || limit < 0)
            isPagination = false;

        // query
        try {
            if (isPagination) {
                PageHelper.offsetPage(offset, limit);
                locationList = locationMapper.selectByRepoID(repoID);
                if (locationList != null) {
                    PageInfo<Location> pageInfo = new PageInfo<>(locationList);
                    total = pageInfo.getTotal();
                } else
                    locationList = new ArrayList<>();
            } else {
                locationList = locationMapper.selectByRepoID(repoID);
                if (locationList != null)
                    total = locationList.size();
                else
                    locationList = new ArrayList<>();
            }
        } catch (PersistenceException e) {
            throw new LocationManageServiceException(e);
        }

        resultSet.put("data", locationList);
        resultSet.put("total", total);
        return resultSet;
    }

    /**
     * 查询所有的货位记录
     *
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @Override
    public Map<String, Object> selectAll() throws LocationManageServiceException {
        return selectAll(-1, -1);
    }

    /**
     * 检查货位信息是否满足要求
     *
     * @param location 货位信息
     * @return 若货位信息满足要求则返回true，否则返回false
     */
    private boolean locationCheck(Location location) {
        if (location != null) {
            if (location.getNo() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加货位记录
     *
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    @UserOperation(value = "添加货位信息")
    @Override
    public boolean addLocation(String locationNOs,Integer repoID, Integer userID) throws LocationManageServiceException {
        try {
            List<String> locationNOList = new ArrayList<>();
            List<Location> locations = new ArrayList<>();
            locationNOList = SPLIT_COMMA.splitToList(locationNOs);
            for(String locationNO : locationNOList){
                Location location = new Location();
                location.setNo(locationNO);
                location.setCreateTime(new Date());
                location.setUpdateTime(new Date());
                location.setPerson(userID);
                location.setRepoID(repoID);
                locations.add(location);
            }
            locationMapper.insertBatch(locations);
            return true;
        } catch (PersistenceException e) {
            throw new LocationManageServiceException(e);
        }
    }

    /**
     * 更新货位记录
     *
     * @param location 货位信息
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    @UserOperation(value = "修改货位信息")
    @Override
    public boolean updateLocation(Location location) throws LocationManageServiceException {

        try {
            // 更新记录
            if (location != null) {
                // 检验
                if (locationCheck(location)) {
                    locationMapper.update(location);
                    return true;
                }
            }
            return false;
        } catch (PersistenceException e) {
            throw new LocationManageServiceException(e);
        }
    }

    /**
     * 删除货位记录
     *
     * @param locationId 货位ID
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    @UserOperation(value = "删除货位信息")
    @Override
    public boolean deleteLocation(Integer locationId) throws LocationManageServiceException {

        try {
            /*// 检查该货位是否有上架信息
            List<StockInDO> stockInDORecord = stockInMapper.selectByLocationID(locationId);
            if (stockInDORecord != null && !stockInDORecord.isEmpty())
                return false;

            // 检查该货位是否有下架信息
            List<StockOutDO> stockOutDORecord = stockOutMapper.selectByLocationId(locationId);
            if (stockOutDORecord != null && !stockOutDORecord.isEmpty())
                return false;*/

            Location location = locationMapper.selectById(locationId);
            String locationNO = "";
            if(location != null ){
                locationNO = location.getNo();
            }
            // 检查该货位是否有库存信息
            List<LocationStorage> locationStorageRecords =  locationStorageMapper.selectBySearch(locationNO,"","", "", "",-1);
            if (locationStorageRecords != null && !locationStorageRecords.isEmpty())
                return false;

            // 删除货位记录
            locationMapper.deleteById(locationId);
            return true;
        } catch (PersistenceException e) {
            throw new LocationManageServiceException(e);
        }
    }

    /**
     * 从文件中导入货位信息
     *
     * @param file 导入信息的文件
     * @return 返回一个Map，其中：key为total代表导入的总记录数，key为available代表有效导入的记录数
     */
    @UserOperation(value = "导入货位信息")
    @Override
    public Map<String, Object> importLocation(MultipartFile file, Integer repoID, Integer userID) throws LocationManageServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        int total = 0;
        int available = 0;

        // 从 Excel 文件中读取
        try {
            List<Location> locationList = ejConvertor.excelReader(Location.class, FileUtil.convertMultipartFileToFile(file));
            if (locationList != null) {
                total = locationList.size();

                // 验证每一条记录
                List<Location> availableList = new ArrayList<>();
                for (Location location : locationList) {
                    if (locationCheck(location)) {
                        location.setCreateTime(new Date());
                        location.setUpdateTime(new Date());
                        location.setPerson(userID);
                        location.setRepoID(repoID);
                        //货位编号中的字母统一成大写
                        String locatonNO = location.getNo();
                        location.setNo(locatonNO.toUpperCase());
                        availableList.add(location);
                    }
                }
                // 保存到数据库
                available = availableList.size();
                if (available > 0) {
                    locationMapper.insertBatch(availableList);
                }
            }
        } catch (PersistenceException | IOException e) {
            throw new LocationManageServiceException(e);
        }

        resultSet.put("total", total);
        resultSet.put("available", available);
        return resultSet;
    }

    /**
     * 导出货位信息到文件中
     *
     * @param location 包含若干条 Supplier 信息的 List
     * @return excel 文件
     */
    @UserOperation(value = "导出货位信息")
    @Override
    public File exportLocation(List<Location> location) {
        if (location == null)
            return null;

        return ejConvertor.excelWriter(Location.class, location);
    }


}
