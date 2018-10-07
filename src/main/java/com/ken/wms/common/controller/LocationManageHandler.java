package com.ken.wms.common.controller;

import com.ken.wms.common.service.Interface.LocationManageService;
import com.ken.wms.common.util.Response;
import com.ken.wms.common.util.ResponseFactory;
import com.ken.wms.domain.Location;
import com.ken.wms.domain.Repository;
import com.ken.wms.domain.Supplier;
import com.ken.wms.domain.UserInfoDTO;
import com.ken.wms.exception.LocationManageServiceException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 货位信息管理请求 Handler
 *
 * @author Ken
 */
@RequestMapping(value = "/**/locationManage")
@Controller
public class LocationManageHandler {

    @Autowired
    private LocationManageService locationManageService;

    private static final String SEARCH_BY_ID = "searchByID";
    private static final String SEARCH_BY_NO = "searchByNO";
    private static final String SEARCH_BY_NAME = "searchByName";
    private static final String SEARCH_ALL = "searchAll";

    /**
     * 通用的记录查询
     *
     * @param searchType 查询类型
     * @param keyWord    查询关键字
     * @param offset     分页偏移值
     * @param limit      分页大小
     * @return 返回一个 Map ，包含所有符合要求的查询结果，以及记录的条数
     */
    private Map<String, Object> query(Integer repoID, String searchType, String keyWord, int offset, int limit) throws LocationManageServiceException {
        Map<String, Object> queryResult = null;

        switch (searchType) {
            /*case SEARCH_BY_ID:
                if (StringUtils.isNumeric(keyWord))
                    queryResult = locationManageService.selectById(Integer.valueOf(keyWord));
                break;*/
            case SEARCH_BY_NO:
                queryResult = locationManageService.selectByNo(repoID, keyWord);
                break;
            case SEARCH_ALL:
                queryResult = locationManageService.selectAllByRepoID(repoID ,offset, limit);
                break;
            default:
                // do other thing
                break;
        }

        return queryResult;
    }

    /**
     * 查询登录人员属于的仓库
     *
     * @return 返回一个map，其中：key 为 result 的值为操作的结果，包括：success 与 error；key 为 data
     * 的值为list
     */
    @RequestMapping(value = "getOwnRepo", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> getOwnRepoInfo() throws LocationManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        String result = Response.RESPONSE_RESULT_ERROR;
        // 获取session中的信息
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute("userInfo");
        boolean isAdmin = false;
        // 设置非管理员请求的仓库ID
        if (currentUser.hasRole("systemAdmin")) {
            isAdmin = true;
        }
        // 获取货物类型
        List<Repository> queryResult = locationManageService.selectOwnRepo(isAdmin,userInfo.getUserID());
        if (queryResult != null) {
            result = Response.RESPONSE_RESULT_SUCCESS;
        }

        // 设置 Response
        responseContent.setResponseResult(result);
        responseContent.setResponseData(queryResult);
        return responseContent.generateResponse();
    }

    /**
     * 搜索货位信息
     *
     * @param searchType 搜索类型
     * @param offset     如有多条记录时分页的偏移值
     * @param limit      如有多条记录时分页的大小
     * @param keyWord    搜索的关键字
     * @return 返回所有符合要求的记录
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "getLocationList", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> getLocationList(@RequestParam("repoID") Integer repoID,@RequestParam("searchType") String searchType,
                                     @RequestParam("offset") int offset, @RequestParam("limit") int limit,
                                     @RequestParam("keyWord") String keyWord) throws LocationManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        List<Supplier> rows = null;
        long total = 0;

        // 查询
        Map<String, Object> queryResult = query(repoID, searchType, keyWord, offset, limit);

        if (queryResult != null) {
            rows = (List<Supplier>) queryResult.get("data");
            total = (long) queryResult.get("total");
        }

        // 设置 Response
        responseContent.setCustomerInfo("rows", rows);
        responseContent.setResponseTotal(total);
        return responseContent.generateResponse();
    }

    /**
     * 添加一条货位信息
     *
     * @param
     * @return 返回一个map，其中：key 为 result表示操作的结果，包括：success 与 error
     */
    @RequestMapping(value = "addLocation", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> addLocation(@RequestBody Location location) throws LocationManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();

        // 获取session中的信息
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute("userInfo");
        String locationNOs = location.getNo();
        int repoID = location.getRepoID();
        // 添加记录
        String result = locationManageService.addLocation(locationNOs,repoID,userInfo.getUserID()) ? Response.RESPONSE_RESULT_SUCCESS : Response.RESPONSE_RESULT_ERROR;

        // 设置 Response
        responseContent.setResponseResult(result);

        return responseContent.generateResponse();
    }

    /**
     * 查询指定 location ID 货位的信息
     *
     * @param locationID 货位ID
     * @return 返回一个map，其中：key 为 result 的值为操作的结果，包括：success 与 error；key 为 data
     * 的值为货位信息
     */
    @RequestMapping(value = "getLocationInfo", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> getLocationInfo(@RequestParam("locationID") Integer locationID) throws LocationManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        String result = Response.RESPONSE_RESULT_ERROR;

        // 获取货位信息
        Location location = null;
        Map<String, Object> queryResult = locationManageService.selectById(locationID);
        if (queryResult != null) {
            location = (Location) queryResult.get("data");
            if (location != null) {
                result = Response.RESPONSE_RESULT_SUCCESS;
            }
        }

        // 设置 Response
        responseContent.setResponseResult(result);
        responseContent.setResponseData(location);
        return responseContent.generateResponse();
    }

    /**
     * 更新货位信息
     *
     * @param location 货位信息
     * @return 返回一个map，其中：key 为 result表示操作的结果，包括：success 与 error
     */
    @RequestMapping(value = "updateLocation", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> updateLocation(@RequestBody Location location) throws LocationManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        // 获取session中的信息
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute("userInfo");
        // 更新
        location.setUpdateTime(new Date());
        location.setPerson(userInfo.getUserID());
        String result = locationManageService.updateLocation(location) ? Response.RESPONSE_RESULT_SUCCESS : Response.RESPONSE_RESULT_ERROR;

        // 设置 Response
        responseContent.setResponseResult(result);
        return responseContent.generateResponse();
    }

    /**
     * 删除货位记录
     *
     * @param locationID 货位ID
     * @return 返回一个map，其中：key 为 result表示操作的结果，包括：success 与 error
     */
    @RequestMapping(value = "deleteLocation", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> deleteLocation(@RequestParam("locationID") Integer locationID) throws LocationManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();

        // 删除
        String result = locationManageService.deleteLocation(locationID) ? Response.RESPONSE_RESULT_SUCCESS : Response.RESPONSE_RESULT_ERROR;

        // 设置 Response
        responseContent.setResponseResult(result);
        return responseContent.generateResponse();
    }

    /**
     * 导入货位信息
     *
     * @param file 保存有货位信息的文件
     * @return 返回一个map，其中：key 为 result表示操作的结果，包括：success 与
     * error；key为total表示导入的总条数；key为available表示有效的条数
     */
    @RequestMapping(value = "importLocation", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> importLocation(@RequestParam("file") MultipartFile file, @RequestParam("repoID") Integer repoID) throws LocationManageServiceException {
        //  初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        String result = Response.RESPONSE_RESULT_ERROR;

        // 读取文件内容
        int total = 0;
        int available = 0;
        if (file != null) {
            // 获取session中的信息
            Subject currentUser = SecurityUtils.getSubject();
            Session session = currentUser.getSession();
            UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute("userInfo");
            Map<String, Object> importInfo = locationManageService.importLocation(file, repoID, userInfo.getUserID());
            if (importInfo != null) {
                total = (int) importInfo.get("total");
                available = (int) importInfo.get("available");
                result = Response.RESPONSE_RESULT_SUCCESS;
            }
        }

        // 设置 Response
        responseContent.setResponseResult(result);
        responseContent.setResponseTotal(total);
        responseContent.setCustomerInfo("available", available);
        return responseContent.generateResponse();
    }

    /**
     * 导出货位信息
     *
     * @param searchType 查找类型
     * @param keyWord    查找关键字
     * @param response   HttpServletResponse
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "exportLocation", method = RequestMethod.GET)
    public void exportLocation(@RequestParam("repoID") Integer repoID,@RequestParam("searchType") String searchType, @RequestParam("keyWord") String keyWord,
                            HttpServletResponse response) throws LocationManageServiceException, IOException {

        String fileName = "locationInfo.xlsx";

        List<Location> locationList = null;
        Map<String, Object> queryResult = query(repoID,searchType, keyWord, -1, -1);

        if (queryResult != null) {
            locationList = (List<Location>) queryResult.get("data");
        }

        // 获取生成的文件
        File file = locationManageService.exportLocation(locationList);

        // 写出文件
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
