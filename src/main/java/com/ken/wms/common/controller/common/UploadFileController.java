package com.ken.wms.common.controller.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ken.wms.common.util.ConvertUtils;
import com.ken.wms.common.util.FileUtil;
import com.ken.wms.common.util.ServletUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@CrossOrigin()
@Controller
@RequestMapping("/upload")
public class UploadFileController {

	private static final Logger logger = LoggerFactory.getLogger("wms_upload");

//    @Autowired
//    private IObsUploadService obsUploadService;
    
    @RequestMapping(value = "/uploadImage.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadImage(DefaultMultipartHttpServletRequest request) {
        return uploadFile(request);
    }

    @RequestMapping(value = "/uploadFile.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadFile(DefaultMultipartHttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        /*String folderPath = request.getParameter("folderPath");
        map.put("code", 0);
        if (folderPath == null || folderPath.equals("")) {
            map.put("msg", "上传文件夹未指定");
            return map;
        }*/
        LinkedHashMap<String, MultipartFile> fileMap = (LinkedHashMap<String, MultipartFile>) request.getFileMap();
        Collection<MultipartFile> files = fileMap.values();
        CommonsMultipartFile mfile = (CommonsMultipartFile) files.toArray()[0];
        mfile.getBytes();
        DiskFileItem fileItem = (DiskFileItem) mfile.getFileItem();
        long maxFileSize = ServletUtils.getRequestParamValue_long(request, "maxFileSize");//KB
        if (maxFileSize > 0 && fileItem.getSize() > maxFileSize * 1024) {
            map.put("msg", "上传文件大小不能超过" + maxFileSize / 1024 + "M");
            return map;
        }

        /*int imageWidth = ServletUtils.getRequestParamValue_int(request, "width");
        int imageHeight = ServletUtils.getRequestParamValue_int(request, "height");
        int imageMaxWidth = ServletUtils.getRequestParamValue_int(request, "maxWidth");
        if (imageMaxWidth > 0 || (imageHeight > 0 && imageWidth > 0)) {
            try {
                BufferedImage sourceImg = ImageIO.read(mfile.getInputStream());
                int realWidth = sourceImg.getWidth();
                int realHeight = sourceImg.getHeight();
                if (imageMaxWidth > 0 && realWidth > imageMaxWidth) {
                    map.put("msg", "上传的图片的尺寸宽度不能超过" + imageMaxWidth + "px，当前图片宽度" + realWidth + "px，请裁剪后再上传。否则无法通过审核噢");
                    return map;
                } else if (imageHeight > 0 && imageWidth > 0 && (realWidth != imageWidth || realHeight != imageHeight)) {
                    map.put("msg", "上传的图片的尺寸为" + imageWidth + "*" + imageHeight + "px，当前图片宽度" + realWidth + "px，高度" + realHeight + "px，请裁剪后再上传。否则无法通过审核噢");
                    return map;
                }
            } catch (Exception exp) {
                map.put("msg", exp.getMessage());
                return map;
            }
        }*/
        String fileName = fileItem.getName();
//        String fileType = fileName.substring(fileName.indexOf(".") + 1).toLowerCase().trim();
        try {
            //String uuid = UUID.randomUUID().toString() + "." + fileType;
//            String url = obsUploadService.uploadFileToObsPublicAccess(mfile.getInputStream(), uuid, folderPath, uuid);
            //上传到服务器所在项目平级目录
            /*String tomcatBin = System.getProperty("user.dir");//Tomcat中运行
            String storagePath = tomcatBin.substring(0,tomcatBin.length()-3)+"upload";*/
            String ctx = request.getSession().getServletContext().getResource("/").getPath();
            String storagePath = "upload";
            String url = storagePath + "/" + FileUtil.uploadFile(mfile,ctx + "/" + storagePath);
            map.put("data", ConvertUtils.newMap(new String[]{"url", "name"}, new String[]{url, fileName}));
            map.put("code", 1);
            return map;
        } catch (Exception e) {
            map.put("msg", "上传失败");
            e.printStackTrace();
            return map;
        }
    }

    public static void main(String[] args) {
        String tomcatBin = "abcd.123";
        System.out.println(tomcatBin.split(".").length);
    }

    /**
     * 异常处理
     *
     * @param ex
     * @Description:
     */
    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex, HttpServletResponse response) {

        StringBuffer sb = new StringBuffer();
        sb.append("<script language='javascript'>history.go(-1);alert('");
        if (ex instanceof MaxUploadSizeExceededException) {
            sb.append("文件大小不应大于" + ((MaxUploadSizeExceededException) ex).getMaxUploadSize() / 1000 + "kb");
        } else {
            sb.append("上传异常！");
        }
        sb.append("！');</script>");
        try {
            System.out.println(sb.toString());
            response.setContentType("text/html; charset=utf-8");
            response.getWriter().println(sb.toString());
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/kingEditorUploadFile")
    @ResponseBody
    public Map<String, Object> kingEditorUploadFile(DefaultMultipartHttpServletRequest request) {
        Map<String, Object> result;
        try {
            result = uploadImage(request);
            if (Integer.parseInt(result.get("code").toString()) == 0) {
                //上传失败
                result = ConvertUtils.newMap(new String[]{"error", "message"}, new Object[]{1, result.get("msg")});
            } else {
                JSONObject data = JSON.parseObject(JSON.toJSONString(result.get("data")));
                result = ConvertUtils.newMap(new String[]{"error", "url"}, new Object[]{0, data.getString("url")});
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = ConvertUtils.newMap(new String[]{"error", "message"}, new Object[]{1, "上传失败"});
        }
        return result;
    }

    @RequestMapping("/layEditUploadFile.do")
    @ResponseBody
    public Map<String, Object> layEditUploadFile(DefaultMultipartHttpServletRequest request) {
        Map<String, Object> result;
        try {
            result = uploadImage(request);
            if (Integer.parseInt(result.get("code").toString()) == 0) {
                //上传失败
                return ConvertUtils.newMap(new String[]{"code", "msg"}, new Object[]{1, result.get("msg")});
            } else {
                JSONObject data = JSON.parseObject(JSON.toJSONString(result.get("data")));
                return ConvertUtils.newMap(new String[]{"code", "data"}, new Object[]{0, ConvertUtils.newMap(new String[]{"src"}, new String[]{data.getString("url")})});
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ConvertUtils.newMap(new String[]{"code", "msg"}, new Object[]{1, "上传失败"});
        }
    }

/*  @RequestMapping(value = {"/getActivityPdfForApp/{id}", "/getActivityPdfForApp/{id}"})
    public void getActivityPdfForApp(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "id", required = false) String id) {
        String fullFileName = "https://cicc-activity.obs.cn-north-1.myhwclouds.com/" + id + ".pdf";
        try {
            FileInputStream hFile = new FileInputStream(fullFileName); // 以byte流的方式打开文件 d:\1.gif
            response.setContentType("application/pdf");
            OutputStream outputStream = response.getOutputStream();
            FileUtil.InputStreamToOutputStream(hFile, outputStream);
        } catch (IOException e) {
            // 解决主机关闭连接问题
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
