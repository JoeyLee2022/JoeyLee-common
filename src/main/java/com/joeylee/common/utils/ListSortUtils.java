package com.joeylee.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 集合排序 工具类
 *
 * @author joeylee
 */
public class ListSortUtils {
    /**
     * List 元素的多字段进行排序
     *
     * @param list 包含要排序元素的 List
     * @param map  要排序的属性和顺序。前面的值优先级高。
     */
    public static <T> void sortByFields(List<T> list, LinkedHashMap<String, Boolean> map) {

        list.sort((o1, o2) -> {
            if (Validator.isEmpty(o1) && Validator.isEmpty(o2)) {
                return 0;
            }
            if (Validator.isEmpty(o1)) {
                return 1;
            }
            if (Validator.isEmpty(o2)) {
                return -1;
            }
            Object o1FieldValue;
            Object o2FieldValue;
            Boolean value;
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                value = map.get(key);
                o1FieldValue = BeanUtil.getFieldValue(o1, key);
                o2FieldValue = BeanUtil.getFieldValue(o2, key);
                if (Validator.isEmpty(o1FieldValue) && Validator.isEmpty(o2FieldValue)) {
                    continue;
                }
                if (Validator.isEmpty(o1FieldValue)) {
                    return 1;
                }
                if (Validator.isEmpty(o2FieldValue)) {
                    return -1;
                }
                if (o1FieldValue.equals(o2FieldValue)) {
                    continue;
                }
                return getResult(o1FieldValue, o2FieldValue, value);
            }
            return 0;
        });
    }

    /**
     * List自定义单字段排序规则,null值放最后,若首字符为汉字，则按拼音首字母排序
     *
     * @param list     包含要排序元素的List
     * @param property 要排序的属性。前面的值优先级高。
     * @param isAsc    true为正序，false为倒序
     */
    public static <T> void sortByField(List<T> list, String property, boolean isAsc) {

        list.sort((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }

            Object o1FieldValue = BeanUtil.getFieldValue(o1, property);
            Object o2FieldValue = BeanUtil.getFieldValue(o2, property);
            if (o1FieldValue == null && o2FieldValue == null) {
                return 0;
            }
            if (o1FieldValue == null) {
                return 1;
            }
            if (o2FieldValue == null) {
                return -1;
            }
            int result = getResult(o1FieldValue, o2FieldValue, isAsc);
            if (result != 0) {
                return result;
            }
            return 0;
        });
    }

    /**
     * 排序规则
     *
     * @param o1FieldValue
     * @param o2FieldValue
     * @param isAsc
     * @return
     */
    private static int getResult(Object o1FieldValue, Object o2FieldValue, boolean isAsc) {

        int result;
        // 如果是数字类型
        if (NumberUtil.isNumber(o1FieldValue.toString()) && NumberUtil.isNumber(o2FieldValue.toString())) {
            result = Double.compare(Double.parseDouble(o1FieldValue.toString()), Double.parseDouble(o2FieldValue.toString()));
        } else {
            // 如果是Date类型
            if (o1FieldValue instanceof Date && o2FieldValue instanceof Date) {
                result = ((Date) o1FieldValue).compareTo((Date) o2FieldValue);
            } else {
                //根据拼音排序
                Comparator<Object> objectComparator = CompareUtil.comparingPinyin(o -> o.toString());
                return objectComparator.compare(o1FieldValue, o2FieldValue);
            }
        }
        if (!isAsc) {
            return -result;
        }
        return result;
    }

    public static void main(String[] args) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<Map<Object, Object>> mapList = CollectionUtil.newArrayList(MapUtil.builder().put("name", "測試繁體字").put("pwd", null).put("age", null).put("birthday", simpleDateFormat.parse("2019-11-28 10:30:00")).build(), MapUtil.builder().put("name", "測試繁體字").put("pwd", null).put("age", 123).put("birthday", simpleDateFormat.parse("2019-11-28 10:30:00")).build(), MapUtil.builder().put("name", "測試繁體字").put("pwd", null).put("age", 25).put("birthday", simpleDateFormat.parse("2019-11-28 10:30:00")).build(), MapUtil.builder().put("name", "泰罗奥特曼").put("pwd", "11").put("age", null).put("birthday", simpleDateFormat.parse("1994-10-28 10:30:00")).build(), MapUtil.builder().put("name", "張學友").put("pwd", null).put("age", 58).put("birthday", simpleDateFormat.parse("1996-05-28 10:30:00")).build(), MapUtil.builder().put("name", "jack").put("pwd", "123").put("age", 2).put("birthday", simpleDateFormat.parse("1997-02-28 10:30:00")).build(), MapUtil.builder().put("name", "Jack").put("pwd", "123").put("age", 25.5).put("birthday", simpleDateFormat.parse("1998-03-28 10:30:00")).build(), MapUtil.builder().put("name", "贝利亚奥特曼").put("pwd", "123").put("age", 12156165).put("birthday", simpleDateFormat.parse("1998-03-28 10:30:00")).build(), MapUtil.builder().put("name", "Rose").put("pwd", "1234").put("age", 12156165).put("birthday", simpleDateFormat.parse("1998-03-28 10:30:00")).build(), MapUtil.builder().build(), null);
        LinkedHashMap<String, Boolean> map = new LinkedHashMap<>();
        map.put("pwd", false);
        map.put("age", false);
        ListSortUtils.sortByFields(mapList, map);
        mapList.forEach(System.out::println);
    }

}
