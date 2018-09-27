package com.ken.wms.common.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 转换工具
 * Created by mr_yi on 2017/8/24.
 */
@SuppressWarnings("rawtypes")
public class ConvertUtils {

    static {
        registerDateConverter();
    }

    /**
     * 定义日期Converter的格式: yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
     */
    private static void registerDateConverter() {
        DateConverter dc = new DateConverter();
        dc.setUseLocaleFormat(true);
        dc.setPatterns(new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "MM/dd/yyyy HH:mm"});
        org.apache.commons.beanutils.ConvertUtils.register(dc, Date.class);
    }


    /**
     * 将字符串转换 long类型数组
     *
     * @param temp  分割的参数
     * @param split 分割的符号
     * @return List<Long>
     */
    public static List<Long> getLongListByString(String temp, String split) {
        List<Long> longs = new ArrayList<>();
        if (StringUtils.isBlank(temp)) return longs;
        for (String str : temp.split(split)) {
            try {
                longs.add(Long.parseLong(str));
            } catch (Exception e) {
                continue;
            }
        }
        return longs;
    }
    /**
     * 将字符串转换 long类型数组
     *
     * @param temp  分割的参数
     * @param split 分割的符号
     * @return List<Long>
     */
    public static List<Long> getOrderLongListByString(String temp, String split) {
        List<Long> longs = new ArrayList<>();
        if (StringUtils.isBlank(temp)) return longs;
        String[] arr=temp.split(split);
        for(int i=0;i<arr.length;i++){
            try {
                longs.add(Long.parseLong(arr[i]));
            } catch (Exception e) {
                continue;
            }
        }
        return longs;
    }

    /**
     * 将字符串转换 long类型数组
     *
     * @param temp  分割的参数
     * @param split 分割的符号
     * @return List<Long>
     */
    public static List<String> getOrderStringListByString(String temp, String split) {
        List<String> longs = new ArrayList<String>();
        if (StringUtils.isBlank(temp)) return longs;
        String[] arr=temp.split(split);
        for(int i=0;i<arr.length;i++){
            try {
                longs.add(arr[i]);
            } catch (Exception e) {
                continue;
            }
        }
        return longs;
    }
    /**
     * 快速构造List
     *
     * @param <E>
     * @param elements
     * @return
     */
    public static <E> List<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList<E>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    /**
     * 将数组转化为MAP
     *
     * @param keys
     * @param values
     * @return
     */
    public static <K, V> Map<K, V> newMap(K[] keys, V[] values) {
        Map<K, V> map = new LinkedHashMap<K, V>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    /**
     * 将Map的valueSet转换为排序过的列表
     *
     * @param map
     * @return
     */
    public static List<Comparable> convertMapValuesToSortedList(Map map) {
        List<Comparable> list = new ArrayList<Comparable>();
        for (Object object : map.values()) {
            list.add((Comparable) object);
        }
        Collections.sort(list);
        return list;
    }

    public static int doubleToInt(double value) {
        return Integer.parseInt(new DecimalFormat("0").format(value));
    }


    /**
     * 得到集合的某个属性值集合
     *
     * @param <T>
     * @param <M>
     * @param objectList
     * @param propertyName
     * @param m
     * @return
     */
    public static <T, M> List<M> getPropertyList(Collection<T> objectList, String propertyName, Class<M> m) {
        List<M> mlist = new ArrayList<M>();
        for (T t : objectList) {
            try {
                mlist.add((M) PropertyUtils.getProperty(t, propertyName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mlist;
    }

    /**
     * 得到以属性为Key,本对象为Value的MAP
     *
     * @param <T>
     * @param <M>
     * @param objectList
     * @param propertyName
     * @param m
     * @return
     */
    public static <T, M> Map<M, T> getMapByList(Collection<T> objectList, String propertyName, Class<M> m) {
        Map<M, T> map = new HashMap<M, T>();
        for (T t : objectList) {
            try {
                map.put((M) PropertyUtils.getProperty(t, propertyName), t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 将double转换为2位小数
     *
     * @param value
     * @return
     */
    public static String get2pointDouble(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value))
            return "0.00";
        return new DecimalFormat("0.00").format(value);
    }

    /**
     * 将List中的数据根据分隔符转换成字符串
     *
     * @param <T>       List中数据的类型，支持泛型
     * @param list      要转换的数据
     * @param separator 转换后数据间的分隔符
     * @return String 根据分隔符转换成的字符串
     */
    public static <T> String convertList2String(List<T> list, String separator) {
        StringBuilder builder = new StringBuilder();
        int size = list == null ? 0 : list.size();
        for (int i = 0; i < size; i++) {
            T t = list.get(i);
            if (i == 0) {
                builder.append(String.valueOf(t));
            } else
                builder.append(separator).append(String.valueOf(t));

        }
        return builder.toString();
    }

    public static <T> T getObjectFromList(Collection<T> objectList, String propertyName, Object value) {
        try {
            for (T t : objectList) {
                if (PropertyUtils.getProperty(t, propertyName).equals(value))
                    return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        for (Long l : getLongListByString("1,a1,b,45,6", ",")) {
            System.out.println(l);
        }
    }

}
