package com.ken.wms.common.controller;

import com.ken.wms.common.service.Interface.LocationStorageManageService;
import com.ken.wms.common.service.Interface.StockRecordManageService;
import com.ken.wms.common.util.Response;
import com.ken.wms.common.util.ResponseFactory;
import com.ken.wms.domain.LocationStorage;
import com.ken.wms.domain.UserInfoDTO;
import com.ken.wms.exception.LocationStorageManageServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 货位库存管理请求处理
 *
 * @author Bea
 */
@Controller
@RequestMapping(value = "/**/locationStorageManage")
public class LocationStorageManageHandler {

    @Autowired
    private LocationStorageManageService locationStorageManageService;
    @Autowired
    private StockRecordManageService stockRecordManageService;

    private static final String SEARCH_BY_GOODS_ID = "searchByGoodsID";
    private static final String SEARCH_BY_GOODS_NO = "searchByGoodsNO";
    private static final String SEARCH_BY_GOODS_NAME = "searchByGoodsName";
    private static final String SEARCH_BY_LOCATION_NO = "searchByLocationNO";
    private static final String SEARCH_BY_GOODS_TYPE = "searchByGoodsType";
    private static final String SEARCH_ALL = "searchAll";

    /**
     * 查询货位库存信息
     *
     * @param searchType       查询类型
     * @param keyword          查询关键字
     * @param repositoryBelong 查询仓库
     * @param offset           分页偏移值
     * @param limit            分页大小
     * @return 结果的一个Map，其中： key为 data 的代表记录数据；key 为 total 代表结果记录的数量
     */
    private Map<String, Object> query(String searchType, String keyword, String selectColor, String selectSize, String repositoryBelong, int offset,
                                      int limit) throws LocationStorageManageServiceException {
        Map<String, Object> queryResult = null;

        switch (searchType) {
            case SEARCH_ALL:
                if (StringUtils.isNumeric(repositoryBelong)) {
                    Integer repositoryID = Integer.valueOf(repositoryBelong);
                    queryResult = locationStorageManageService.selectAllBySearchPage("","","","", "", repositoryID, offset, limit);
                } else {
                    queryResult = locationStorageManageService.selectAllBySearchPage("","","","", "", -1, offset, limit);
                }
                break;
            case SEARCH_BY_GOODS_NO:
                if (StringUtils.isNumeric(repositoryBelong)) {
                    Integer repositoryID = Integer.valueOf(repositoryBelong);
                    queryResult = locationStorageManageService.selectAllBySearchPage("",keyword, "", selectColor, selectSize, repositoryID, offset, limit);
                } else
                    queryResult = locationStorageManageService.selectAllBySearchPage("",keyword, "", selectColor, selectSize, -1, offset, limit);
                break;
            case SEARCH_BY_GOODS_NAME:
                if (StringUtils.isNumeric(repositoryBelong)) {
                    Integer repositoryID = Integer.valueOf(repositoryBelong);
                    queryResult = locationStorageManageService.selectAllBySearchPage("","", keyword, selectColor, selectSize, repositoryID, offset, limit);
                } else
                    queryResult = locationStorageManageService.selectAllBySearchPage("","", keyword, selectColor, selectSize, -1, offset, limit);
                break;
            case SEARCH_BY_LOCATION_NO:
                if (StringUtils.isNumeric(repositoryBelong)) {
                    Integer repositoryID = Integer.valueOf(repositoryBelong);
                    queryResult = locationStorageManageService.selectAllBySearchPage(keyword,"", "", selectColor, selectSize, repositoryID, offset, limit);
                } else
                    queryResult = locationStorageManageService.selectAllBySearchPage(keyword,"", "", selectColor, selectSize, -1, offset, limit);
                break;
            default:
                // do other thing
                break;
        }

        return queryResult;
    }

    /**
     * 可指定仓库对货位库存信息查询
     *
     * @param keyword          查询关键字
     * @param searchType       查询类型
     * @param repositoryBelong 查询所属的仓库
     * @param offset           分页偏移值
     * @param limit            分页大小
     * @return 结果的一个Map，其中： key为 rows 的代表记录数据；key 为 total 代表结果记录的数量
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "getLocationStorageList", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> getLocationStorageList(@RequestParam("keyword") String keyword,
                                                 @RequestParam("searchType") String searchType,
                                                 @RequestParam("selectColor") String selectColor,
                                                 @RequestParam("selectSize") String selectSize,
                                                 @RequestParam("repositoryBelong") String repositoryBelong,
                                                 @RequestParam("offset") int offset, @RequestParam("limit") int limit) throws LocationStorageManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();

        List<LocationStorage> rows;
        long total = 0;
        long totalNum = 0;

        // query
        Map<String, Object> queryResult = query(searchType, keyword, selectColor, selectSize, repositoryBelong, offset, limit);
        if (queryResult != null) {
            rows = (List<LocationStorage>) queryResult.get("data");
            total = (long) queryResult.get("total");
            totalNum = (long) queryResult.get("totalNum");
        } else
            rows = new ArrayList<>();

        // 设置 Response
        responseContent.setCustomerInfo("rows", rows);
        responseContent.setCustomerInfo("totalNum", totalNum);
        responseContent.setResponseTotal(total);
        return responseContent.generateResponse();
    }

    /**
     * 查询货位库存信息，查询所属的仓库为session保存的信息
     *
     * @param keyword    查询关键字
     * @param searchType 查询类型
     * @param offset     分页偏移值
     * @param limit      分页大小
     * @param request    请求
     * @return 结果的一个Map，其中： key为 rows 的代表记录数据；key 为 total 代表结果记录的数量
     */
    /*@SuppressWarnings("unchecked")
    @RequestMapping(value = "getLocationStorageList", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> getLocationStorageList(@RequestParam("keyword") String keyword,
                                       @RequestParam("searchType") String searchType, @RequestParam("offset") int offset,
                                       @RequestParam("limit") int limit, HttpServletRequest request) throws LocationStorageManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();

        List<LocationStorage> rows = null;
        long total = 0;

        HttpSession session = request.getSession();
        UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute("userInfo");
        Integer repositoryID = userInfo.getRepositoryBelong();
        if (repositoryID > 0) {
            Map<String, Object> queryResult = query(searchType, keyword, null, null, repositoryID.toString(), offset, limit);
            if (queryResult != null) {
                rows = (List<LocationStorage>) queryResult.get("data");
                total = (long) queryResult.get("total");
            }
        }

        if (rows == null)
            rows = new ArrayList<>();

        // 设置 Response
        responseContent.setCustomerInfo("rows", rows);
        responseContent.setResponseTotal(total);
        return responseContent.generateResponse();
    }*/

    /**
     * 添加一条货位库存信息
     *
     * @return 返回一个map，其中：key 为 result表示操作的结果，包括：success 与 error
     */
    @RequestMapping(value = "addLocationStorageRecord", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> addLocationStorageRecord(@RequestBody Map<String, Object> params) throws LocationStorageManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        String isSuccess = Response.RESPONSE_RESULT_ERROR;
        boolean isAvailable = true;

        String goodsID = (String) params.get("goodsID");
        String repositoryID = (String) params.get("repositoryID");
        String number = (String) params.get("number");

        if (StringUtils.isBlank(goodsID) || !StringUtils.isNumeric(goodsID))
            isAvailable = false;
        if (StringUtils.isBlank(repositoryID) || !StringUtils.isNumeric(repositoryID))
            isAvailable = false;
        if (StringUtils.isBlank(number) || !StringUtils.isNumeric(number))
            isAvailable = false;

        if (isAvailable) {
            LocationStorage locationStorage = new LocationStorage();
            locationStorage.setGoodsID(Integer.valueOf(goodsID));
            //TODO
            isSuccess = locationStorageManageService.addLocationStorage(locationStorage) ? Response.RESPONSE_RESULT_SUCCESS : Response.RESPONSE_RESULT_ERROR;
        }

        // 设置 Response
        responseContent.setResponseResult(isSuccess);
        return responseContent.generateResponse();
    }

    /**
     * 更新货位库存信息
     *
     * @return 返回一个map，其中：key 为 result表示操作的结果，包括：success 与 error
     */
    @RequestMapping(value = "updateLocationStorageRecord", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> updateLocationStorageRecord(@RequestBody Map<String, Object> params) throws LocationStorageManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        boolean isAvailable = true;
        String result = Response.RESPONSE_RESULT_ERROR;

        String locationStorageID = (String) params.get("locationStorageID");
        String number = (String) params.get("number");

        if (StringUtils.isBlank(locationStorageID) || !StringUtils.isNumeric(locationStorageID))
            isAvailable = false;
        if (StringUtils.isBlank(number) || !StringUtils.isNumeric(number))
            isAvailable = false;

        if (isAvailable) {
            result = locationStorageManageService.updateStorageByNum(Integer.valueOf(locationStorageID), Integer.valueOf(number)) ? Response.RESPONSE_RESULT_SUCCESS : Response.RESPONSE_RESULT_ERROR;
        }

        // 设置 Response
        responseContent.setResponseResult(result);
        return responseContent.generateResponse();
    }

    /**
     * 删除一条货位库存信息
     *
     * @param locationStorageID
     * @return 返回一个map，其中：key 为 result表示操作的结果，包括：success 与 error
     */
    @RequestMapping(value = "deleteLocationStorageRecord", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> deleteLocationStorageRecord(@RequestParam("locationStorageID") String locationStorageID) throws LocationStorageManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();

        String result = Response.RESPONSE_RESULT_ERROR;
        boolean isAvailable = true;

        if (StringUtils.isBlank(locationStorageID) || !StringUtils.isNumeric(locationStorageID))
            isAvailable = false;

        if (isAvailable) {
            result = locationStorageManageService.deleteLocationStorage(Integer.valueOf(locationStorageID))
                    ? Response.RESPONSE_RESULT_SUCCESS : Response.RESPONSE_RESULT_ERROR;
        }

        // 设置 Response
        responseContent.setResponseResult(result);
        return responseContent.generateResponse();
    }

    /**
     * 导入货位库存信息
     *
     * @param file 保存有货位库存信息的文件
     * @return 返回一个map，其中：key 为 result表示操作的结果，包括：success 与
     * error；key为total表示导入的总条数；key为available表示有效的条数
     */
    @RequestMapping(value = "importLocationStorageRecord", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> importLocationStorageRecord(@RequestParam("file") MultipartFile file) throws LocationStorageManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        String result = Response.RESPONSE_RESULT_ERROR;

        int total = 0;
        int available = 0;
        String errorTip = "";

        if (file != null) {
            Map<String, Object> importInfo = locationStorageManageService.importLocationStorage(file);
            if (importInfo != null) {
                total = (int) importInfo.get("total");
                errorTip = (String)importInfo.get("errorTip");
                if(errorTip == null || "".equals(errorTip)){
                    available = (int) importInfo.get("available");
                    result = Response.RESPONSE_RESULT_SUCCESS;
                }else{
                    result = Response.RESPONSE_RESULT_ERROR;
                }
            }
        }

        // 设置 Response
        responseContent.setResponseResult(result);
        responseContent.setResponseTotal(total);
        responseContent.setCustomerInfo("available", available);
        responseContent.setCustomerInfo("errorTip", errorTip);
        return responseContent.generateResponse();
    }

    /**
     * 导出货位库存信息
     *
     * @param searchType       查询类型
     * @param keyword          查询关键字
     * @param repositoryBelong 查询所属仓库
     * @param request          请求
     * @param response         响应
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "exportLocationStorageRecord", method = RequestMethod.GET)
    public void exportLocationStorageRecord(@RequestParam("searchType") String searchType,
                                    @RequestParam("keyword") String keyword,
                                    @RequestParam("selectColor") String selectColor,
                                    @RequestParam("selectSize") String selectSize,
                                    @RequestParam(value = "repositoryBelong", required = false) String repositoryBelong,
                                    HttpServletRequest request, HttpServletResponse response) throws LocationStorageManageServiceException, IOException {
        String fileName = "locationStorage.xlsx";

        HttpSession session = request.getSession();
        UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute("userInfo");
        Integer sessionRepositoryBelong = userInfo.getRepositoryBelong();
        if (sessionRepositoryBelong > 0)
            repositoryBelong = sessionRepositoryBelong.toString();

        List<LocationStorage> storageList = null;
        Map<String, Object> queryResult = query(searchType, keyword, selectColor, selectSize, repositoryBelong, -1, -1);
        if (queryResult != null)
            storageList = (List<LocationStorage>) queryResult.get("data");

        File file = locationStorageManageService.exportLocationStorage(storageList);
        if (file != null) {
            // 设置响应头
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            FileInputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[8192];

            int len;
            while ((len = inputStream.read(buffer, 0, buffer.length)) > 0) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }

            inputStream.close();
            outputStream.close();

        }
    }
}
