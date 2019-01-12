package com.ken.wms.common.controller;

import com.google.common.base.Splitter;
import com.ken.wms.common.service.Interface.LocationRecordManageService;
import com.ken.wms.common.service.Interface.LocationStorageManageService;
import com.ken.wms.common.util.Response;
import com.ken.wms.common.util.ResponseFactory;
import com.ken.wms.domain.LocationStorage;
import com.ken.wms.domain.StockRecordDTO;
import com.ken.wms.domain.UserInfoDTO;
import com.ken.wms.exception.LocationManageServiceException;
import com.ken.wms.exception.LocationRecordManageServiceException;
import com.ken.wms.exception.LocationStorageManageServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品出入库管理请求Handler
 *
 * @author Bea
 * @since 017/4/5.
 */
@Controller
@RequestMapping(value = "locationRecordManage")
public class LocationRecordManageHandler {

    private static final Splitter SPLIT_SEMICOLON = Splitter.on(";").trimResults().omitEmptyStrings();
    private static final Splitter SPLIT_COMMA = Splitter.on(",").trimResults().omitEmptyStrings();
    @Autowired
    private LocationRecordManageService locationRecordManageService;
    @Autowired
    private LocationStorageManageService locationStorageManageService;

    /**
     * 货物入库操作
     *
     * @param goodsID         货物ID
     * @param repositoryIDStr 仓库ID
     * @param goodsDetail     入库明细："颜色,尺码,数量;颜色,尺码,数量;"
     * @return 返回一个map，key为result的值表示操作是否成功
     */
    @RequestMapping(value = "locationUp", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> locationUp(@RequestParam("goodsID") String goodsID,
                                   @RequestParam("goodsName") String goodsName,
                                   @RequestParam("goodsDetail") String goodsDetail,
                                   @RequestParam(value = "repositoryID", required = false) String repositoryIDStr,
                                   @RequestParam("remark") String remark
    ) throws LocationRecordManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        String result = Response.RESPONSE_RESULT_ERROR;
        boolean authorizeCheck = true;
        boolean argumentCheck = true;
        Integer repositoryID = null;

        // 参数检查
        if (repositoryIDStr != null) {
            if (StringUtils.isNumeric(repositoryIDStr)) {
                repositoryID = Integer.valueOf(repositoryIDStr);
            } else {
                argumentCheck = false;
                responseContent.setResponseMsg("request argument error");
            }
        }

        // 获取session中的信息
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute("userInfo");
        Integer personID = userInfo == null ? -1 : userInfo.getUserID();
        Integer repositoryIDBelong = userInfo == null ? -1 : userInfo.getRepositoryBelong();

        // 设置非管理员请求的仓库ID
        if (!currentUser.hasRole("systemAdmin")) {
            if (repositoryIDBelong < 0) {
                authorizeCheck = false;
                responseContent.setResponseMsg("You are not authorized");
            } else {
                repositoryID = repositoryIDBelong;
            }
        }

        // 执行 Service
        if (authorizeCheck && argumentCheck) {
            if (locationRecordManageService.locationUpOperation(Integer.valueOf(goodsID), goodsName, goodsDetail, repositoryID, personID, remark)) {
                result = Response.RESPONSE_RESULT_SUCCESS;
            }
        }

        // 设置 Response
        responseContent.setResponseResult(result);
        return responseContent.generateResponse();
    }

    /**
     * 货位出库时，校验该仓库的货位上是否有一定数量的货物
     *
     * @return 返回一个map，key为result的值表示操作是否成功
     */
    @RequestMapping(value = "checkLocationStorage", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> checkLocationStorage(@RequestParam("goodsID") String goodsID,
                                 @RequestParam("goodsColor") String goodsColor,
                                 @RequestParam("goodsSize") String goodsSize,
                                 @RequestParam("goodsNum") String goodsNum,
                                 @RequestParam("locationNO") String locationNO,
                                 @RequestParam(value = "repositoryID", required = false) String repositoryIDStr
                                 ) throws LocationStorageManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        String resultValue = "1";
        boolean authorizeCheck = true;
        boolean argumentCheck = true;
        Integer repositoryID = null;

        // 参数检查
        if (repositoryIDStr != null) {
            if (StringUtils.isNumeric(repositoryIDStr)) {
                repositoryID = Integer.valueOf(repositoryIDStr);
            } else {
                argumentCheck = false;
                responseContent.setResponseMsg("request argument error");
            }
        }

        // 获取 session 中的信息
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute("userInfo");
        Integer personID = userInfo == null ? -1 : userInfo.getUserID();
        Integer repositoryIDBelong = userInfo == null ? -1 : userInfo.getRepositoryBelong();

        // 设置非管理员请求的仓库ID
        if (!currentUser.hasRole("systemAdmin")) {
            if (repositoryIDBelong < 0) {
                authorizeCheck = false;
                responseContent.setResponseMsg("You are not authorized");
            } else {
                repositoryID = repositoryIDBelong;
            }
        }

        if (authorizeCheck && argumentCheck) { //单条记录校验
            int ret = locationStorageManageService.checkLocationStorageService(Integer.valueOf(goodsID), goodsColor, goodsSize, Long.parseLong(goodsNum), locationNO, repositoryID);
            resultValue = String.valueOf(ret);
        }

        // 设置 Response
        responseContent.setResponseResult(resultValue);
        return responseContent.generateResponse();
    }

    /**
     * 货物出库操作
     *
     * @return 返回一个map，key为result的值表示操作是否成功
     */
    @RequestMapping(value = "locationDown", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> locationDown(@RequestParam("goodsID") String goodsID,
                                 @RequestParam("goodsName") String goodsName,
                                 @RequestParam("goodsDetail") String goodsDetail,
                                 @RequestParam(value = "repositoryID", required = false) String repositoryIDStr,
                                 @RequestParam("remark") String remark
                                 ) throws LocationRecordManageServiceException,LocationStorageManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        String result = Response.RESPONSE_RESULT_ERROR;
        boolean authorizeCheck = true;
        boolean argumentCheck = true;
        Integer repositoryID = null;

        // 参数检查
        if (repositoryIDStr != null) {
            if (StringUtils.isNumeric(repositoryIDStr)) {
                repositoryID = Integer.valueOf(repositoryIDStr);
            } else {
                argumentCheck = false;
                responseContent.setResponseMsg("request argument error");
            }
        }

        // 获取 session 中的信息
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute("userInfo");
        Integer personID = userInfo == null ? -1 : userInfo.getUserID();
        Integer repositoryIDBelong = userInfo == null ? -1 : userInfo.getRepositoryBelong();

        // 设置非管理员请求的仓库ID
        if (!currentUser.hasRole("systemAdmin")) {
            if (repositoryIDBelong < 0) {
                authorizeCheck = false;
                responseContent.setResponseMsg("You are not authorized");
            } else {
                repositoryID = repositoryIDBelong;
            }
        }
        String data = "";
        if (authorizeCheck && argumentCheck) {
            //再次校验
            List<String> goodsSingle = SPLIT_SEMICOLON.splitToList(goodsDetail);
            int lineData = 1;
            int retCheck = 1;
            for(String goodss : goodsSingle){
                List<String> goodsStr = SPLIT_COMMA.splitToList(goodss);
                String goodsColor = goodsStr.get(0);
                String goodsSize = goodsStr.get(1);
                String goodsNum = goodsStr.get(2);
                String locationNO = goodsStr.get(3);
                retCheck = locationStorageManageService.checkLocationStorageService(Integer.parseInt(goodsID), goodsColor, goodsSize,Long.parseLong(goodsNum), locationNO, repositoryID);
                if(retCheck != 1){ //出现问题
                    data = retCheck + ";" + lineData; //前面是返回结果标识，后面是第几行出现问题
                    break;
                }
                lineData++;
            }
            if(retCheck == 1){
                if (locationRecordManageService.locationDownOperation(Integer.parseInt(goodsID), goodsName, goodsDetail, repositoryID, personID, remark)){
                    result = Response.RESPONSE_RESULT_SUCCESS;
                }
            }
        }
        // 设置 Response
        responseContent.setResponseResult(result);
        responseContent.setResponseData(data);
        return responseContent.generateResponse();
    }

    /**
     * 查询出出入库记录
     *
     * @param repositoryIDStr 查询记录所对应的仓库ID
     * @param endDateStr      查询的记录起始日期
     * @param startDateStr    查询的记录结束日期
     * @param limit           分页大小
     * @param offset          分页偏移值
     * @return 返回一个Map，其中：Key为rows的值代表所有记录数据，Key为total的值代表记录的总条数
     */
    @SuppressWarnings({"SingleStatementInBlock", "unchecked"})
    @RequestMapping(value = "searchLocationRecord", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getLocationRecord(@RequestParam("searchType") String upOrDown,
                                          @RequestParam("goodsNO") String goodsNO,
                                          @RequestParam("goodsName") String goodsName,
                                          @RequestParam("goodsColor") String goodsColor,
                                          @RequestParam("goodsSize") String goodsSize,
                                          @RequestParam("repositoryID") String repositoryIDStr,
                                          @RequestParam("personID") String personIDStr,
                                          @RequestParam("startDate") String startDateStr,
                                          @RequestParam("endDate") String endDateStr,
                                       @RequestParam("limit") int limit,
                                       @RequestParam("offset") int offset) throws ParseException, LocationRecordManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        List<StockRecordDTO> rows = null;
        long total = 0;

        // 参数检查
        String regex = "([0-9]{4})-([0-9]{2})-([0-9]{2})";
        boolean startDateFormatCheck = (StringUtils.isEmpty(startDateStr) || startDateStr.matches(regex));
        boolean endDateFormatCheck = (StringUtils.isEmpty(endDateStr) || endDateStr.matches(regex));

        if (startDateFormatCheck && endDateFormatCheck) {
            Integer repositoryID = -1;
            Integer personID = -1;
            if (StringUtils.isNumeric(repositoryIDStr)) {
                repositoryID = Integer.valueOf(repositoryIDStr);
            }
            if (StringUtils.isNumeric(personIDStr)) {
                personID = Integer.valueOf(personIDStr);
            }

            // 转到 Service 执行查询
            Map<String, Object> queryResult = locationRecordManageService.selectLocationRecordPage(goodsNO, goodsName, goodsColor, goodsSize, repositoryID, personID, startDateStr, endDateStr, upOrDown, offset, limit);
            if (queryResult != null) {
                rows = (List<StockRecordDTO>) queryResult.get("data");
                total = (long) queryResult.get("total");
            }
        } else
            responseContent.setResponseMsg("Request argument error");

        if (rows == null)
            rows = new ArrayList<>(0);

        responseContent.setCustomerInfo("rows", rows);
        responseContent.setResponseTotal(total);
        return responseContent.generateResponse();
    }
}
