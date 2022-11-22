package com.joeylee.common.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * map 工具类
 *
 * @author joeylee
 */
@Slf4j
public class MapUtils {


    public static String formatString(Map<?, ?> map) {
        return formatString(map, false);
    }

    public static String formatString(Map<?, ?> map, boolean isOrder) {
        return formatString(map, isOrder, false);
    }

    public static String formatString(Map<?, ?> map, boolean isOrder, boolean inTheMiddle) {
        return formatString(map, isOrder, inTheMiddle, 0);
    }

    /**
     * 格式化 输出 成字符串
     *
     * @param map         源数据
     * @param isOrder     是否排序
     * @param inTheMiddle 是否居中
     * @param appendSpace 追加空格
     * @return
     */
    public static String formatString(Map<?, ?> map, boolean isOrder, boolean inTheMiddle, int appendSpace) {
        if (MapUtil.isEmpty(map)) {
            return null;
        }
        if (isOrder) {
            map = MapUtil.sort(map);
        }
        //获取key长度的最大值
        Set<? extends Map.Entry<?, ?>> entries = map.entrySet();
        int maxKeySize = entries.stream().mapToInt(entry -> StrUtil.bytes(entry.getKey().toString(), CharsetUtil.CHARSET_GBK).length).max().getAsInt();
        //获取value长度的最大值
        int maxValueSize = entries.stream().mapToInt(entry -> StrUtil.bytes(entry.getValue().toString(), CharsetUtil.CHARSET_GBK).length).max().getAsInt();

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<?, ?> entry : entries) {
            stringBuilder.append("|");
            Object key = entry.getKey();
            Object value = entry.getValue();

            int keyLength = maxKeySize - StrUtil.bytes(key.toString(), CharsetUtil.CHARSET_GBK).length + appendSpace;
            appendString(inTheMiddle, keyLength, stringBuilder, key);

            stringBuilder.append("|");
            int valueLength = maxValueSize - StrUtil.bytes(value.toString(), CharsetUtil.CHARSET_GBK).length + appendSpace;
            appendString(inTheMiddle, valueLength, stringBuilder, value);
            stringBuilder.append("|");
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public static List<String> formatList(Map<?, ?> map) {
        return formatList(map, false);
    }

    public static List<String> formatList(Map<?, ?> map, boolean isOrder) {
        return formatList(map, isOrder, false);
    }

    public static List<String> formatList(Map<?, ?> map, boolean isOrder, boolean inTheMiddle) {
        return formatList(map, isOrder, inTheMiddle, 0);
    }

    /**
     * 格式化 输出 成集合
     *
     * @param map         源数据
     * @param isOrder     是否排序
     * @param inTheMiddle 是否居中
     * @param appendSpace 追加空格
     * @return
     */
    public static List<String> formatList(Map<?, ?> map, boolean isOrder, boolean inTheMiddle, int appendSpace) {
        if (MapUtil.isEmpty(map)) {
            return Collections.EMPTY_LIST;
        }
        if (isOrder) {
            map = MapUtil.sort(map);
        }
        //获取key长度的最大值
        Set<? extends Map.Entry<?, ?>> entries = map.entrySet();
        int maxKeySize = entries.stream().mapToInt(entry -> StrUtil.bytes(entry.getKey().toString(), CharsetUtil.CHARSET_GBK).length).max().getAsInt();
        //获取value长度的最大值
        int maxValueSize = entries.stream().mapToInt(entry -> StrUtil.bytes(entry.getValue().toString(), CharsetUtil.CHARSET_GBK).length).max().getAsInt();

        List<String> stringList = new ArrayList<>();
        StringBuilder stringBuilder;

        for (Map.Entry<?, ?> entry : entries) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("|");
            Object key = entry.getKey();
            Object value = entry.getValue();

            int keyLength = maxKeySize - StrUtil.bytes(key.toString(), CharsetUtil.CHARSET_GBK).length + appendSpace;
            appendString(inTheMiddle, keyLength, stringBuilder, key);
            stringBuilder.append("|");
            int valueLength = maxValueSize - StrUtil.bytes(value.toString(), CharsetUtil.CHARSET_GBK).length + appendSpace;
            appendString(inTheMiddle, valueLength, stringBuilder, value);
            stringBuilder.append("|");
            stringList.add(stringBuilder.toString());
        }
        return stringList;
    }

    private static void appendString(boolean inTheMiddle, int length, StringBuilder stringBuilder, Object key) {
        if (length == 0) {
            stringBuilder.append(key);
            return;
        }
        if (inTheMiddle) {
            int half = length / 2;
            for (int j = 0; j < half; j++) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(key);
            if (length % 2 != 0) {
                half += 1;
            }
            for (int j = 0; j < half; j++) {
                stringBuilder.append(" ");
            }
        } else {
            stringBuilder.append(key);
            for (int j = 0; j < length; j++) {
                stringBuilder.append(" ");
            }
        }
    }
}