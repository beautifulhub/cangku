package com.ken.wms.common.service.Interface;


import com.ken.wms.domain.LocationStorage;
import com.ken.wms.domain.Storage;
import com.ken.wms.exception.LocationStorageManageServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 货位库存信息管理 service
 *
 * @author Bea
 */
public interface LocationStorageManageService {

    /**
     * 返回所有的货位库存记录---不分页
     *
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectAllBySearch(String goodsNO,String goodsName,String goodsColor, String goodsSize, Integer repositoryID) throws LocationStorageManageServiceException;

    /**
     * 分页返回所有的货位库存记录
     *
     * @param offset 分页偏移值
     * @param limit  分页大小
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    Map<String, Object> selectAllBySearchPage(String locationNO, String goodsNO,String goodsName,String goodsColor, String goodsSize, Integer repositoryID, int offset, int limit) throws LocationStorageManageServiceException;


    /**
     * 添加一条货位库存记录
     *
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    boolean addLocationStorage(LocationStorage locationStorage) throws LocationStorageManageServiceException;

    /**
     * 更新一条货位库存记录（修改数量）
     *
     * @param storageID
     * @param number
     * @return
     * @throws LocationStorageManageServiceException
     */
    boolean updateStorageByNum(Integer storageID, long number) throws LocationStorageManageServiceException;

    /**
     * 为指定的货位库存记录增加指定数目
     *
     * @param goodsStr      货物Str
     * @param repositoryID 仓库ID
     * @return 返回一个 boolean 值，若值为true表示数目增加成功，否则表示增加失败
     */
    boolean locationStorageIncrease(Integer goodsID, List<String> goodsStr, Integer repositoryID) throws LocationStorageManageServiceException;

    /**
     * 为指定的货物货位库存记录减少指定的数目
     *
     * @param goodsID      货物ID
     * @param repositoryID 仓库ID
     * @return 返回一个 boolean 值，若值为 true 表示数目减少成功，否则表示增加失败
     */
    boolean locationStorageDecrease(Integer goodsID, String goodsColor, String goodsSize, long goodsNum, String locationNO, Integer repositoryID) throws LocationStorageManageServiceException;

    /**
     * 货位下架时，校验该仓库的货位上是否有一定数量的货物
     */
    int checkLocationStorageService(Integer goodsID, String goodsColor, String goodsSize, long goodsNum, String locationNO, Integer repositoryID) throws LocationStorageManageServiceException;

    /**
     * 删除一条货位库存记录
     *
     * @return 返回一个boolean值，值为true代表更新成功，否则代表失败
     */
    boolean deleteLocationStorage(Integer locationStorageID) throws LocationStorageManageServiceException;

    /**
     * 导入货位库存记录
     *
     * @param file 保存有的货位库存记录的文件
     * @return 返回一个Map，其中：key为total代表导入的总记录数，key为available代表有效导入的记录数
     */
    Map<String, Object> importLocationStorage(MultipartFile file) throws LocationStorageManageServiceException;

    /**
     * 导出货位库存记录
     *
     * @param locationStorageList 保存有货位库存记录的List
     * @return excel 文件
     */
    File exportLocationStorage(List<LocationStorage> locationStorageList);
}
