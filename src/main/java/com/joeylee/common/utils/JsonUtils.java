package com.joeylee.common.utils;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.List;

/**
 * Json 工具类
 *
 * @author joeylee
 */
public class JsonUtils {
    public static boolean isJSONObj(Object json) {
        return json instanceof JSONObject;
    }

    public static boolean isJSONArray(Object json) {
        return json instanceof JSONArray;
    }

    public static void getJSONValue(JSONObject json, String k, List<String> list) {
        for (Object j : json.keySet()) {
            if (isJSONObj(json.get(j))) {
                //是对象
                JSONObject j2 = JSONUtil.parseObj(json.get(j).toString());
                getJSONValue(j2, k, list);
            } else if (isJSONArray(json.get(j))) {
                JSONArray j3 = JSONUtil.parseArray(json.get(j).toString());
                //是数组
                getJSONValue(j3, k, list);
            } else if (j.equals(k)) {
                //是字符串
                list.add(json.get(j).toString());
            }
        }
    }

    public static void getJSONValue(JSONArray json, String k, List<String> list) {
        for (Object j : json) {
            if (isJSONObj(j)) {
                //是对象
                JSONObject j2 = JSONUtil.parseObj(j.toString());
                getJSONValue(j2, k, list);
            } else if (isJSONArray(j)) {
                //是数组
                JSONArray j3 = JSONUtil.parseArray(j.toString());
                getJSONValue(j3, k, list);
            }
        }
    }

    /**
     * 递归找到第一个value并返回
     */
    public static String getJSONValue(JSONArray jsonArray, String findKey) {
        for (Object key : jsonArray) {
            if (isJSONObj(key)) {
                //是对象
                return getJSONValue((JSONObject) key, findKey);
            } else if (isJSONArray(key)) {
                //是数组
                return getJSONValue((JSONArray) key, findKey);
            }
        }
        return null;
    }

    /**
     * 递归找到第一个value并返回
     */
    public static String getJSONValue(JSONObject jsonObject, String findKey) {
        for (Object key : jsonObject.keySet()) {
            if (key.equals(findKey)) {
                //匹配成功
                return jsonObject.get(findKey).toString();
            } else if (isJSONObj(jsonObject.get(key))) {
                //是对象
                return getJSONValue(jsonObject.getJSONObject(key.toString()), findKey);
            } else if (isJSONArray(jsonObject.get(key))) {
                //是数组
                return getJSONValue(jsonObject.getJSONArray(key.toString()), findKey);
            }
        }
        return null;
    }

}
