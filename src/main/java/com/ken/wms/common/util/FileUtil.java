package com.ken.wms.common.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 文件操作工具类
 *
 * @author Ken
 * @since 2017/4/22.
 */
public class FileUtil {

    /**
     * 将 org.springframework.web.multipart.MultipartFile 类型的文件转换为 java.io.File 类型的文件
     *
     * @param multipartFile org.springframework.web.multipart.MultipartFile 类型的文件
     * @return 返回转换后的 java.io.File 类型的文件
     * @throws IOException IOException
     */
    public static File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File convertedFile = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(convertedFile);
        return convertedFile;
    }

    /**
     * 移动指定文件夹内的全部文件
     *
     * @param from
     *            要移动的文件目录
     * @param to
     *            目标文件目录
     * @throws Exception
     */
    public static void fileMove(String from, String to) throws Exception {
        try {
            File dir = new File(from);
            // 文件一览
            File[] files = dir.listFiles();
            if (files == null)
                return;
            // 目标
            File moveDir = new File(to);
            if (!moveDir.exists()) {
                moveDir.mkdirs();
            }
            // 文件移动
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    fileMove(files[i].getPath(), to + "\\" + files[i].getName());
                    // 成功，删除原文件
                    files[i].delete();
                }
                File moveFile = new File(moveDir.getPath() + "\\" + files[i].getName());
                // 目标文件夹下存在的话，删除
                if (moveFile.exists()) {
                    moveFile.delete();
                }
                files[i].renameTo(moveFile);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
                file.delete();
            }
        } else {
            System.out.println("所删除的文件不存在！");
        }
    }

    /**
     * 剪切文件夹
     *
     * @param filePath
     * @param newFilePath
     */
    private static boolean _cutFolder(String filePath, String newFilePath) {
        File file = new File(filePath);
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                return file.renameTo(new File(newFilePath));
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File newFile = new File(newFilePath);
                if (!newFile.exists()) {
                    newFile.mkdirs();
                }
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                boolean flag = true;
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    if (!_cutFolder(filePath + "/" + files[i].getName(), newFilePath + "/" + files[i].getName())) {
                        flag = false;
                    }
                }
                return flag;
            } else {
                return false;
            }
        } else {
            System.out.println("所移动的的文件不存在！" + '\n');
            return false;
        }
    }

    /**
     * 剪切文件夹
     *
     * @param filePath
     * @param newFilePath
     */
    public static boolean cutFolder(String filePath, String newFilePath) {
        File newFile = new File(newFilePath);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
        return _cutFolder(filePath, newFilePath);
    }

    public static String decodeString(String srcStr) {
        try {
            return URLDecoder.decode(srcStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return srcStr;
        }
    }

    /**
     *
     * 通过文件的相对路径获取文件字节数组
     */
    public static ArrayList<byte[]> getBytesFromFileList(ArrayList<String> pathlist) {
        ArrayList<byte[]> bytearraylist = new ArrayList<byte[]>();
        for (int i = 0; i < pathlist.size(); i++) {
            bytearraylist.add(getBytesFromFile(formatPath(pathlist.get(i))));
        }
        return bytearraylist;
    }

    /**
     *
     * 通过文件的相对路径获取文件字节数组
     *
     * @param path
     */
    public static byte[] getBytesFromFile(String path) {
        return getBytesFromFile(formatPath(path));
    }

    // /**
    // *
    // * 通过文件的相对路径获取文件字节数组
    // *
    // * @param path
    // */
    // public static ArrayList<byte[]> getBytesFromFileListForFlex(
    // ArrayList<String> pathlist) {
    // ArrayList<byte[]> bytearraylist = new ArrayList<byte[]>();
    // for (int i = 0; i < pathlist.size(); i++) {
    // bytearraylist.add(getBytesFromFile(new File(AsunSystem.WebRoot
    // + AsunSystem.properties.get("flexRoot") + pathlist.get(i))));
    // }
    // return bytearraylist;
    // }
    //
    // /**
    // *
    // * 通过文件的相对路径获取文件字节数组
    // *
    // * @param path
    // */
    // public static byte[] getBytesFromFileForFlex(String path) {
    // return getBytesFromFile(new File(AsunSystem.WebRoot
    // + AsunSystem.properties.get("flexRoot") + path));
    // }

    public static String writeFileFromBytes(String path, byte[] bytes) {
        writeFileFromBytes(formatPath(path), bytes);
        return path;
    }

    public static String writeBigFileFromBytes(String path, byte[] bytes) {
        writeBigFileFromBytes(formatPath(path), bytes);
        return path;
    }

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    /**
     *
     * 格式化路径
     *
     * @param path
     */
    public static File formatPath(String path) {
        path = path.replaceAll("\\\\", "/");
        File file = new File(path);
        mkdirs(path);
        return file;
    }

    /**
     * 为该路径创建目录
     *
     * @param path
     */
    private static void mkdirs(String path) {
        (new File(path.substring(0, path.lastIndexOf('/') + 1))).mkdirs();
    }

    public static void writeFileFromBytes(File file, byte[] bytes) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            try {
                out.write(bytes);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void writeBigFileFromBytes(File file, byte[] bytes) {
        try {
            FileOutputStream out = new FileOutputStream(file, true);
            try {
                out.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件转化为字节数组
     */
    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(new FileInputStream(f));
            byte[] bytIn = new byte[(int) f.length()];
            bis.read(bytIn);
            bis.close();
            return bytIn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 文件转化为字节数组
     */
    public static byte[] getBytesFromFile2(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * JSP页面中文转码 转两次
     *
     * @param str
     */
    public static String encode(String str) {
        try {
            return java.net.URLEncoder.encode(java.net.URLEncoder.encode(str, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "/WEB-INF/page/share/error.html";
        }
    }

    /**
     * JSP页面转码 转一次
     *
     * @param str
     */
    public static String encode1(String str) {
        try {
            return java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "/WEB-INF/page/share/error.html";
        }
    }

    /**
     * JSP页面解码
     *
     * @param str
     */
    public static String decode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * JSP页面解码2次
     *
     * @param str
     */
    public static String decode1(String str) {
        try {
            return URLDecoder.decode(URLDecoder.decode(str, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void InputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws Exception{
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);

        byte[] bys = new byte[1024 * 32];
        int len = 0;
        try {
            while ((len = bis.read(bys)) != -1) {
                bos.write(bys, 0, len);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                bos.close();
                bis.close();
            } catch (IOException e) {
                throw e;
            }
        }

    }

    /**
     * 将文件上传到指定路径下
     * @param multipartFile
     * @param path
     * @return
     * @throws Exception
     */
    public static String uploadFile(MultipartFile multipartFile, String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
        String oriFileName = multipartFile.getOriginalFilename();
        String fileType = oriFileName.substring(oriFileName.indexOf(".") + 1).toLowerCase().trim();
        String fileName = UUID.randomUUID() + "." + fileType;
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path + File.separator + fileName));
        byte[] bs = new byte[1024];
        int len;
        while ((len = fileInputStream.read(bs)) != -1) {
            bos.write(bs, 0, len);
        }
        bos.flush();
        bos.close();
        return fileName;
    }

}
