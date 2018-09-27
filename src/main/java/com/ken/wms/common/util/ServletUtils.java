/**
 * Copyright (c) 2005-2010 springside.org.cn
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * $Id: ServletUtils.java 1211 2010-09-10 16:20:45Z calvinxiu $
 */
package com.ken.wms.common.util;

import com.github.pagehelper.Page;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.ui.Model;
import org.springframework.util.Assert;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;


/**
 * Http与Servlet工具类.
 *
 * @author calvin
 */
public class ServletUtils {

    //-- Content Type 定义 --//
    public static final String TEXT_TYPE = "text/plain";
    public static final String JSON_TYPE = "application/json";
    public static final String XML_TYPE = "text/xml";
    public static final String HTML_TYPE = "text/html";
    public static final String JS_TYPE = "text/javascript";
    public static final String EXCEL_TYPE = "application/vnd.ms-excel";

    //-- Header 定义 --//
    public static final String AUTHENTICATION_HEADER = "Authorization";

    //-- 常用数值定义 --//
    public static final long ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;

    /**
     * 设置客户端缓存过期时间 的Header.
     */
    public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
        //Http 1.0 header
        response.setDateHeader("Expires", System.currentTimeMillis() + expiresSeconds * 1000);
        //Http 1.1 header
        response.setHeader("Cache-Control", "private, max-age=" + expiresSeconds);
    }

    /**
     * 设置禁止客户端缓存的Header.
     */
    public static void setDisableCacheHeader(HttpServletResponse response) {
        //Http 1.0 header
        response.setDateHeader("Expires", 1L);
        response.addHeader("Pragma", "no-cache");
        //Http 1.1 header
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
    }

    /**
     * 设置LastModified Header.
     */
    public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
        response.setDateHeader("Last-Modified", lastModifiedDate);
    }

    /**
     * 设置Etag Header.
     */
    public static void setEtag(HttpServletResponse response, String etag) {
        response.setHeader("ETag", etag);
    }

    /**
     * 根据浏览器If-Modified-Since Header, 计算文件是否已被修改.
     * <p>
     * 如果无修改, checkIfModify返回false ,设置304 not modify status.
     *
     * @param lastModified 内容的最后修改时间.
     */
    public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response,
                                               long lastModified) {
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if ((ifModifiedSince != -1) && (lastModified < ifModifiedSince + 1000)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return false;
        }
        return true;
    }

    /**
     * 根据浏览器 If-None-Match Header, 计算Etag是否已无效.
     * <p>
     * 如果Etag有效, checkIfNoneMatch返回false, 设置304 not modify status.
     *
     * @param etag 内容的ETag.
     */
    public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
        String headerValue = request.getHeader("If-None-Match");
        if (headerValue != null) {
            boolean conditionSatisfied = false;
            if (!"*".equals(headerValue)) {
                StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");

                while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
                    String currentToken = commaTokenizer.nextToken();
                    if (currentToken.trim().equals(etag)) {
                        conditionSatisfied = true;
                    }
                }
            } else {
                conditionSatisfied = true;
            }

            if (conditionSatisfied) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setHeader("ETag", etag);
                return false;
            }
        }
        return true;
    }

    /**
     * 设置让浏览器弹出下载对话框的Header.
     *
     * @param fileName 下载后的文件名.
     */
    public static void setFileDownloadHeader(HttpServletResponse response, String fileName) {
        try {
            //中文文件名支持
            String encodedfileName = new String(fileName.getBytes(), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName + "\"");
        } catch (UnsupportedEncodingException e) {
        }
    }

    /**
     * 取得带相同前缀的Request Parameters.
     * <p>
     * 返回的结果的Parameter名已去除前缀.
     */
    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
        Assert.notNull(request, "Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        if (prefix == null) {
            prefix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, values);
                } else {
                    params.put(unprefixed, values[0]);
                }
            }
        }
        return params;
    }

    /**
     * 对Http Basic验证的 Header进行编码.
     */
    public static String encodeHttpBasic(String userName, String password) {
        String encode = userName + ":" + password;
        return "Basic " + EncodeUtils.base64Encode(encode.getBytes());
    }


    /**
     * 从Request中获取参数如果为空则为0
     *
     * @param request
     * @param parameterName
     * @return
     */
    public static int getRequestParamValue_int(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        if (StringUtils.isEmpty(value))
            return 0;
        else return Integer.parseInt(value);
    }

    /**
     * 从Request中获取参数如果为空则为0
     *
     * @param request
     * @param parameterName
     * @return
     */
    public static long getRequestParamValue_long(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        if (value == null)
            return 0L;
        else return Long.parseLong(value);
    }

    /**
     * 处理空格
     *
     * @param request
     * @param parameterName
     * @return
     */
    public static String getParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
    }

    /**
     * 将页面参数设置到对象的同名属性
     *
     * @param bean
     * @param request
     * @param parameterName
     */
    public static void setParameterToBean(Object bean, HttpServletRequest request, String parameterName) {
        try {
            String parameterValue = getParameter(request, parameterName);
            Object value = parameterValue;
            Class propertyClass = PropertyUtils.getPropertyType(bean, parameterName);
            if (propertyClass == Integer.class || propertyClass == int.class) {
                value = StringUtils.isBlank(parameterValue) ? 0 : Integer.parseInt(parameterValue);
            } else if (propertyClass == Long.class || propertyClass == long.class) {
                value = StringUtils.isBlank(parameterValue) ? 0 : Long.parseLong(parameterValue);
            } else if (propertyClass == Boolean.class || propertyClass == boolean.class) {
                value = StringUtils.isBlank(parameterValue) ? false : Boolean.parseBoolean(parameterValue);
            } else if (propertyClass == Double.class || propertyClass == double.class) {
                value = StringUtils.isBlank(parameterValue) ? 0 : Double.parseDouble(parameterValue);
            }
            PropertyUtils.setProperty(bean, parameterName, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPage(HttpServletRequest request, int defaultPageNum, int defaultPageSize, Page page) {
        int pageNum = NumberUtils.toInt(request.getParameter("pageNum"), defaultPageNum);
        int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), defaultPageSize);
        page.setPageNum(pageNum).setPageSize(pageSize);
    }

    /**
     * 将request中的某个参数放到Model
     *
     * @param parameterName
     * @param request
     * @param model
     */
    public static void transforParameter(String parameterName, HttpServletRequest request, Model model) {
        model.addAttribute(parameterName, getParameter(request, parameterName));
    }

    public static String getDegreeName(Integer degree) {

        if (degree == 1) {
            return "博士";
        }
        if (degree == 2) {
            return "硕士";
        }
        if (degree == 3) {
            return "本科";
        }
        if (degree == 4) {
            return "专科";
        }
        if (degree == 5) {
            return "其他";
        }
        return "其他";
    }

    public static Integer getDegreeId(String name) {
        if (name.equals("博士")) {
            return 1;
        }
        if (name.equals("硕士")) {
            return 2;
        }
        if (name.equals("本科")) {
            return 3;
        }
        if (name.equals("专科")) {
            return 4;
        }
        if (name.equals("其他")) {
            return 5;
        }
        return 5;
    }

    // 获取客户端ip
    public static String getRemoteAddress(HttpServletRequest request) {
        String ip = ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        ip = ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
        return ip;
    }

    public static boolean getReturnValue(long total, int pageNum, int pageSize) {
        if (((pageNum * pageSize - total) >= pageSize || (pageNum * pageSize) - total == 0)&&pageNum>1) {
            return false;
        }
        return true;
    }

    public static String getOS(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String os = "";
        //=================OS Info=======================
        if (userAgent.toLowerCase().indexOf("windows") >= 0 || userAgent.toLowerCase().indexOf("mac") >= 0) {
            os = "Web CN";
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("Language".equals(cookie.getName())) {

                        os = "zh_CN".equals(cookie.getValue()) ? "Web CN" : "Web EN";
                        break;
                    }

                }
            }


        } else if (userAgent.toLowerCase().indexOf("android") >= 0) {
            os = "Android App";
        } else if (userAgent.toLowerCase().indexOf("iphone") >= 0) {
            os = "iOS App";
        } else if (userAgent.toLowerCase().contains("micromessenger")) {
            os = "公众号";
        } else {
            os = "其他";
        }
        return os;
    }
}
