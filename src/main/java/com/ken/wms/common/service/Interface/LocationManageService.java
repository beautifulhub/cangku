package com.ken.wms.common.service.Interface;


import com.ken.wms.domain.Location;
import com.ken.wms.domain.Repository;
import com.ken.wms.exception.LocationManageServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 货位信息管理 service
 *
 * @author Bea
 */
public interface LocationManageService {

    /**
     * 返回指定location ID 的货位记录
     *
     * @param locationId 货位ID
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectById(Integer locationId) throws LocationManageServiceException;

    /**
     * 返回指定location NO 的货位记录
     *
     * @param locationNo 货位ID
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectByNo(Integer repoID, String locationNo) throws LocationManageServiceException;

    /**
     * 返回指定location NO模糊 的货位记录
     *
     * @param locationNo 货位ID
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectByLikeNo(Integer repoID, String locationNo) throws LocationManageServiceException;

    /**
     * 分页查询货位记录
     *
     * @param offset 分页的偏移值
     * @param limit  分页的大小
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectAll(int offset, int limit) throws LocationManageServiceException;

    /**
     * 分页查询货位记录
     *
     * @param offset 分页的偏移值
     * @param limit  分页的大小
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectAllByRepoID(int repoID , int offset, int limit) throws LocationManageServiceException;

    /**
     * 查询所有的货位记录
     *
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectAll() throws LocationManageServiceException;

    /**
     * 添加货位记录
     *
     * @param locationNOs 货位编号
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    boolean addLocation(String locationNOs,Integer repoID, Integer userID) throws LocationManageServiceException;

    /**
     * 更新货位记录
     *
     * @param location 货位信息
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    boolean updateLocation(Location location) throws LocationManageServiceException;

    /**
     * 删除货位记录
     *
     * @param locationId 货位ID
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    boolean deleteLocation(Integer locationId) throws LocationManageServiceException;

    /**
     * 从文件中导入货位信息
     *
     * @param file 导入信息的文件
     * @return 返回一个Map，其中：key为total代表导入的总记录数，key为available代表有效导入的记录数
     */
    Map<String, Object> importLocation(MultipartFile file, Integer repoID, Integer userID) throws LocationManageServiceException;

    /**
     * 导出货位信息到文件中
     *
     * @param location 包含若干条 信息的 List
     * @return excel 文件
     */
    File exportLocation(List<Location> location);


}
