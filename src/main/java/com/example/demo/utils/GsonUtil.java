package com.example.demo.utils;


import com.example.demo.http.bean.BaseResponseObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.example.demo.utils.type.TypeBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description: GsonUtil <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/9 13:05 <br>
 */

public class GsonUtil {
    private static Gson gson = new Gson();

    private GsonUtil() {
    }

    /**
     * 将object对象转成json字符串
     *
     * @param object
     * @return
     */
    public static String gsonString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * 将gsonString转成泛型bean
     *
     * @param gsonString
     * @param clazz
     * @return
     */
    public static <T> T gsonToBean(String gsonString, Class<T> clazz) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, clazz);
        }
        return t;
    }

    /**
     * 转成list
     * 解决泛型问题
     *
     * @param gsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> gsonToArray(String gsonString, Class<T> clazz) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(gsonString).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, clazz));
        }
        return list;
    }


    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> gsonToArrayMaps(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }


    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> gsonToMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    /**
     * 泛型
     *
     * @param gsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <S, T extends BaseResponseObject> T gsonToResponseObject(String gsonString, Class<? extends BaseResponseObject> clazz, Class<S> cls) {
        Type type = TypeBuilder
                .newInstance(clazz)
                .addTypeParam(cls)
                .build();
        return gson.fromJson(gsonString, type);
    }

    /**
     * 泛型 数组
     *
     * @param gsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <S, T extends BaseResponseObject<List<S>>> T gsonToResponseArray(String gsonString, Class<? extends BaseResponseObject> clazz, Class<S> cls) {
        Type type = TypeBuilder
                .newInstance(clazz)
                .beginSubType(List.class)
                .addTypeParam(cls)
                .endSubType()
                .build();
        return gson.fromJson(gsonString, type);
    }
}

