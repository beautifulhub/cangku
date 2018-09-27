/**
 * Copyright (c) 2005-2010 springside.org.cn
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * $Id: EncodeUtils.java 1211 2010-09-10 16:20:45Z calvinxiu $
 */
package com.ken.wms.common.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 各种格式的编码加码工具类.
 *
 * 集成Commons-Codec,Commons-Lang及JDK提供的编解码方法.
 *
 * @author calvin
 */
public class EncodeUtils {

    private static final String DEFAULT_URL_ENCODING = "UTF-8";

    /**
     * Hex编码.
     */
    public static String hexEncode(byte[] input) {
        return Hex.encodeHexString(input);
    }

    /**
     * Hex解码.
     */
    public static byte[] hexDecode(String input) {
        try {
            return Hex.decodeHex(input.toCharArray());
        } catch (DecoderException e) {
            throw new IllegalStateException("Hex Decoder exception", e);
        }
    }

    /**
     * Base64编码.
     */
    public static String base64Encode(byte[] input) {
        return new String(Base64.encodeBase64(input));
    }

    /**
     * Base64编码, URL安全(将Base64中的URL非法字符如+,/=转为其他字符, 见RFC3548).
     */
    public static String base64UrlSafeEncode(byte[] input) {
        return Base64.encodeBase64URLSafeString(input);
    }

    /**
     * Base64解码.
     */
    public static byte[] base64Decode(String input) {
        return Base64.decodeBase64(input);
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     */
    public static String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }

    /**
     * Html 转码.
     */
    public static String htmlEscape(String html) {
        return StringEscapeUtils.escapeHtml4(html);
    }

    /**
     * Html 解码.
     */
    public static String htmlUnescape(String htmlEscaped) {
        return StringEscapeUtils.unescapeHtml4(htmlEscaped);
    }

    /**
     * Xml 转码.
     */
    public static String xmlEscape(String xml) {
        return StringEscapeUtils.escapeXml(xml);
    }

    /**
     * Xml 解码.
     */
    public static String xmlUnescape(String xmlEscaped) {
        return StringEscapeUtils.unescapeXml(xmlEscaped);
    }

    /**
     * MD5编码
     * @param originstr
     * @return
     */
    public static String ecodeByMD5(String originstr) {
        String result = null;
        char hexDigits[] = {//用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        if (originstr != null) {
            try {
                //返回实现指定摘要算法的 MessageDigest 对象
                MessageDigest md = MessageDigest.getInstance("MD5");
                //使用utf-8编码将originstr字符串编码并保存到source字节数组
                byte[] source = originstr.getBytes("utf-8");
                //使用指定的 byte 数组更新摘要
                md.update(source);
                //通过执行诸如填充之类的最终操作完成哈希计算，结果是一个128位的长整数
                byte[] tmp = md.digest();
                //用16进制数表示需要32位
                char[] str = new char[32];
                for (int i = 0, j = 0; i < 16; i++) {
                    //j表示转换结果中对应的字符位置
                    //从第一个字节开始，对 MD5 的每一个字节
                    //转换成 16 进制字符
                    byte b = tmp[i];
                    //取字节中高 4 位的数字转换
                    //无符号右移运算符>>> ，它总是在左边补0
                    //0x代表它后面的是十六进制的数字. f转换成十进制就是15
                    str[j++] = hexDigits[b >>> 4 & 0xf];
                    // 取字节中低 4 位的数字转换
                    str[j++] = hexDigits[b & 0xf];
                }
                result = new String(str);//结果转换成字符串用于返回
            } catch (NoSuchAlgorithmException e) {
                //当请求特定的加密算法而它在该环境中不可用时抛出此异常
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                //不支持字符编码异常
                e.printStackTrace();
            }
        }
        return result;

    }


    /**
     * 编码串的解码（异或方式来解码）
     *
     * @param encryptText
     *            密文
     * @param key
     *            密钥
     * @return 解码后的明文字符串
     */
    public static String getEncrypt(String encryptText, String key) {
        char[] snNum = new char[encryptText.length() / 3];
        StringBuilder sb = new StringBuilder();
        for (int i = 0, j = 0; i < encryptText.length() / 3; i++, j++) {
            if (j == key.length())
                j = 0;
            int n = Integer.parseInt(encryptText.substring(i * 3, i * 3 + 3));
            snNum[i] = (char) ((char) n ^ key.charAt(j));
        }

        for (int k = 0; k < encryptText.length() / 3; k++) {
            sb.append(snNum[k]);
        }
        return sb.toString();
    }

    /**
     * 使用异或进行简单的编码
     *
     * @param plainText
     *            明文
     * @param key
     *            密钥
     * @return 编码后的字符串
     */
    public static String setEncrypt(String plainText, String key) {
        int[] snNum = new int[plainText.length()];
        StringBuilder encryptText = new StringBuilder();
        String temp = "";
        for (int i = 0, j = 0; i < plainText.length(); i++, j++) {
            if (j == key.length())
                j = 0;
            snNum[i] = plainText.charAt(i) ^ key.charAt(j);
        }

        for (int k = 0; k < plainText.length(); k++) {
            if (snNum[k] < 10) {
                temp = "00" + snNum[k];
            } else {
                if (snNum[k] < 100) {
                    temp = "0" + snNum[k];
                } else {
                    //100~999
                    temp = "" + snNum[k];
                }
            }
            encryptText.append(temp);
        }
        return encryptText.toString();
    }

    /**
     * 去掉null
     *
     * @param des
     * @return
     */
    public static String trimNull(Object des) {
        try {
            if (des == null)
                return "";
            else
                return des.toString().trim();
        } catch (Exception npe) {
            return "";
        }
    }


    public static byte[] encry_RC4_byte(String data, String key) {
        if (data == null || key == null) {
            return null;
        }
        byte b_data[] = data.getBytes();
        return RC4Base(b_data, key);
    }

    public static String decry_RC4(String data, String key) {
        if (data == null || key == null) {
            return null;
        }
        return new String(RC4Base(HexString2Bytes(data), key));
    }


    public static String decry_RC4_byte(byte[] data, String key) {
        if (data == null || key == null) {
            return null;
        }
        return new String(RC4Base(data, key));
    }

    public static String encry_RC4_string(String data, String key) {
        if (data == null || key == null) {
            return null;
        }
        return toHexString(asString(encry_RC4_byte(data, key)));
    }

    private static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch & 0xFF);
            if (s4.length() == 1) {
                s4 = '0' + s4;
            }
            str = str + s4;
        }
        return str;// 0x表示十六进制
    }

    private static String asString(byte[] buf) {
        StringBuffer strbuf = new StringBuffer(buf.length);
        for (int i = 0; i < buf.length; i++) {
            strbuf.append((char) buf[i]);
        }
        return strbuf.toString();
    }

    private static byte[] HexString2Bytes(String src) {
        int size = src.length();
        byte[] ret = new byte[size / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < size / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private static byte uniteBytes(byte src0, byte src1) {
        char _b0 = (char) Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (char) (_b0 << 4);
        char _b1 = (char) Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    private static byte[] initKey(String aKey) {
        byte[] b_key = aKey.getBytes();
        byte state[] = new byte[256];

        for (int i = 0; i < 256; i++) {
            state[i] = (byte) i;
        }
        int index1 = 0;
        int index2 = 0;
        if (b_key == null || b_key.length == 0) {
            return null;
        }
        for (int i = 0; i < 256; i++) {
            index2 = ((b_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
            byte tmp = state[i];
            state[i] = state[index2];
            state[index2] = tmp;
            index1 = (index1 + 1) % b_key.length;
        }
        return state;
    }

    private static byte[] RC4Base(byte[] input, String mKkey) {
        int x = 0;
        int y = 0;
        byte key[] = initKey(mKkey);
        int xorIndex;
        byte[] result = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xff;
            y = ((key[x] & 0xff) + y) & 0xff;
            byte tmp = key[x];
            key[x] = key[y];
            key[y] = tmp;
            xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
            result[i] = (byte) (input[i] ^ key[xorIndex]);
        }
        return result;
    }

}
