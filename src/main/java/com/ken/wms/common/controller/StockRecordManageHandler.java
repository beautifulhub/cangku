package com.ken.wms.common.controller;

import com.ken.wms.common.service.Interface.StockRecordManageService;
import com.ken.wms.common.util.Response;
import com.ken.wms.common.util.ResponseFactory;
import com.ken.wms.domain.StockRecordDTO;
import com.ken.wms.domain.Storage;
import com.ken.wms.domain.UserInfoDTO;
import com.ken.wms.exception.StockRecordManageServiceException;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品进出货管理请求Handler
 *
 * @author Bea
 * @since 017/4/5.
 */
@Controller
@RequestMapping(value = "stockRecordManage")
public class StockRecordManageHandler {

    @Autowired
    private StockRecordManageService stockRecordManageService;

    /**
     * 货物出货操作
     *
     * @param customerID      客户ID
     * @param repositoryIDStr 仓库ID
     * @return 返回一个map，key为result的值表示操作是否成功
     */
    @RequestMapping(value = "stockOut", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> stockOut(@RequestParam("customerID") Integer customerID,
                                 @RequestParam("goodsNO") String goodsNO,
                                 @RequestParam("goodsName") String goodsName,
                                 @RequestParam("goodsDetail") String goodsDetail,
                                 @RequestParam(value = "repositoryID", required = false) String repositoryIDStr,
                                 @RequestParam("remark") String remark
                                 ) throws StockRecordManageServiceException {
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
        String personInCharge = userInfo == null ? "none" : userInfo.getUserName();
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

        if (authorizeCheck && argumentCheck) {
            if (stockRecordManageService.stockOutOperation(customerID, goodsNO, goodsName, goodsDetail, repositoryID, personInCharge, remark))
                result = Response.RESPONSE_RESULT_SUCCESS;
        }

        // 设置 Response
        responseContent.setResponseResult(result);
        return responseContent.generateResponse();
    }

    /**
     * 货物进货操作
     *
     * @param supplierID      供应商ID
     * @param goodsNO         货物编号
     * @param repositoryIDStr 仓库ID
     * @param goodsDetail     进货明细："颜色,尺码,数量;颜色,尺码,数量;"
     * @return 返回一个map，key为result的值表示操作是否成功
     */
    @RequestMapping(value = "stockIn", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> stockIn(@RequestParam("supplierID") Integer supplierID,
                                @RequestParam("goodsNO") String goodsNO,
                                @RequestParam("goodsName") String goodsName,
                                @RequestParam("goodsDetail") String goodsDetail,
                                @RequestParam(value = "repositoryID", required = false) String repositoryIDStr,
                                @RequestParam("remark") String remark
                                ) throws StockRecordManageServiceException {
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
        String personInCharge = userInfo == null ? "none" : userInfo.getUserName();
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
            if (stockRecordManageService.stockInOperation(supplierID, goodsNO, goodsName, goodsDetail, repositoryID, personInCharge, remark)) {
                result = Response.RESPONSE_RESULT_SUCCESS;
            }
        }

        // 设置 Response
        responseContent.setResponseResult(result);
        return responseContent.generateResponse();
    }

    /**
     * 查询进出货记录
     *
     * @param searchType      查询类型（查询所有或仅查询进货记录或仅查询出货记录）
     * @param repositoryIDStr 查询记录所对应的仓库ID
     * @param endDateStr      查询的记录起始日期
     * @param startDateStr    查询的记录结束日期
     * @param limit           分页大小
     * @param offset          分页偏移值
     * @return 返回一个Map，其中：Key为rows的值代表所有记录数据，Key为total的值代表记录的总条数
     */
    @SuppressWarnings({"SingleStatementInBlock", "unchecked"})
    @RequestMapping(value = "searchStockRecord", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getStockRecord(@RequestParam("searchType") String searchType,
                                       @RequestParam("repositoryID") String repositoryIDStr,
                                       @RequestParam("startDate") String startDateStr,
                                       @RequestParam("endDate") String endDateStr,
                                       @RequestParam("limit") int limit,
                                       @RequestParam("offset") int offset) throws ParseException, StockRecordManageServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        List<StockRecordDTO> rows = null;
        long total = 0;

        // 参数检查
        String regex = "([0-9]{4})-([0-9]{2})-([0-9]{2})";
        boolean startDateFormatCheck = (StringUtils.isEmpty(startDateStr) || startDateStr.matches(regex));
        boolean endDateFormatCheck = (StringUtils.isEmpty(endDateStr) || endDateStr.matches(regex));
        boolean repositoryIDCheck = (StringUtils.isEmpty(repositoryIDStr) || StringUtils.isNumeric(repositoryIDStr));

        if (startDateFormatCheck && endDateFormatCheck && repositoryIDCheck) {
            Integer repositoryID = -1;
            if (StringUtils.isNumeric(repositoryIDStr)) {
                repositoryID = Integer.valueOf(repositoryIDStr);
            }

            // 转到 Service 执行查询
            Map<String, Object> queryResult = stockRecordManageService.selectStockRecord(repositoryID, startDateStr, endDateStr, searchType, offset, limit);
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

    /**
     * 导出进出货信息
     *
     * @param searchType       查询类型
     * @param repositoryIDStr 查询所属仓库
     * @param request          请求
     * @param response         响应
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "exportJCHRecord", method = RequestMethod.GET)
    public void exportJCHRecord(@RequestParam("searchType") String searchType,
                                @RequestParam("repositoryID") String repositoryIDStr,
                                @RequestParam("startDate") String startDateStr,
                                @RequestParam("endDate") String endDateStr,
                                HttpServletRequest request, HttpServletResponse response) throws StockRecordManageServiceException, IOException {
        String fileName = "jchRecord.xlsx";

        HttpSession session = request.getSession();
        UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute("userInfo");
        Integer sessionRepositoryBelong = userInfo.getRepositoryBelong();
        if (sessionRepositoryBelong > 0)
            repositoryIDStr = sessionRepositoryBelong.toString();
        // 参数检查
        String regex = "([0-9]{4})-([0-9]{2})-([0-9]{2})";
        boolean startDateFormatCheck = (StringUtils.isEmpty(startDateStr) || startDateStr.matches(regex));
        boolean endDateFormatCheck = (StringUtils.isEmpty(endDateStr) || endDateStr.matches(regex));
        boolean repositoryIDCheck = (StringUtils.isEmpty(repositoryIDStr) || StringUtils.isNumeric(repositoryIDStr));
        List<StockRecordDTO> stockRecordList = null;
        if (startDateFormatCheck && endDateFormatCheck && repositoryIDCheck) {
            Integer repositoryID = -1;
            if (StringUtils.isNumeric(repositoryIDStr)) {
                repositoryID = Integer.valueOf(repositoryIDStr);
            }
            Map<String, Object> queryResult = stockRecordManageService.selectStockRecord(repositoryID, startDateStr, endDateStr, searchType, -1, -1);
            if (queryResult != null) {
                stockRecordList = (List<StockRecordDTO>) queryResult.get("data");
            }
        }
        File file = stockRecordManageService.exportJCHRecord(stockRecordList);
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
