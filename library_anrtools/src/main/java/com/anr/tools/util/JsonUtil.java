package com.anr.tools.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * <p>
 * Date: 2019-12-20
 * Copyright: Copyright (c) 2010-2019
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * @Author:
 */
public class JsonUtil {

    private static final String TAG = "JsonUtil";
    private static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        //Add strategy if needed.
        gson = builder.create();
    }

    public static String jsonFromObject(Object object) {
        if (object == null) {
            return "{}";
        }
        try {
            return gson.toJson(object);
        } catch (Throwable e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public static <T> String jsonFromList(List<T> list) {
        if (null == list) {
            return "[]";
        }
        try {
            return gson.toJson(list);
        } catch (Throwable e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public static <T> T objectFromJson(String json, Class<T> classType) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        if (classType == null) {
            return null;
        }
        try {
            return gson.fromJson(json, classType);
        } catch (Throwable e) {
            // e.printStackTrace();防止以前旧数据格式引起的报错
            return null;
        }
    }

    public static <T> ArrayList<T> jsonToList(String json, Class<T> classOfType) {
        ArrayList<T> listOfType = new ArrayList<>();

        Type type;
        if (classOfType.isPrimitive() || classOfType == String.class) {
            type = new TypeToken<ArrayList<JsonPrimitive>>() {
            }.getType();

            ArrayList<JsonPrimitive> jsonObjs = gson.fromJson(json, type);

            for (JsonPrimitive jsonObj : jsonObjs) {
                listOfType.add(gson.fromJson(jsonObj, classOfType));
            }
        } else {
            type = new TypeToken<ArrayList<JsonObject>>() {
            }.getType();
            ArrayList<JsonObject> jsonObjs = gson.fromJson(json, type);

            for (JsonObject jsonObj : jsonObjs) {
                listOfType.add(gson.fromJson(jsonObj, classOfType));
            }
        }

        return listOfType;
    }

    /**
     * Desc:用于转换List<String>类型数据
     * <p>
     * Author: []
     * Date: 2019-10-25
     *
     * @param <T>         泛型
     * @param json        json字符串
     * @param classOfType 目标类型
     * @return array list
     */
    public static <T> ArrayList<T> jsonToListString(String json, Class<T> classOfType) {
        ArrayList<T> listOfType = new ArrayList<>();

        Type type;
        if (classOfType.isPrimitive()) {
            type = new TypeToken<ArrayList<JsonPrimitive>>() {
            }.getType();

            ArrayList<JsonPrimitive> jsonObjects = gson.fromJson(json, type);

            for (JsonPrimitive jsonObj : jsonObjects) {
                listOfType.add(gson.fromJson(jsonObj, classOfType));
            }
        } else {
            type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> jsonObjects = gson.fromJson(json, type);

            for (String str : jsonObjects) {
                String jsonObj = gson.toJson(str);//将gson转化为json,防止带空格字符串解析报错
                listOfType.add(gson.fromJson(jsonObj, classOfType));
            }
        }

        return listOfType;
    }

    /**
     * 判断一个json字符串是不是为空，可能是空字符串，或者空的json串
     *
     * @param json json字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String json) {
        if (TextUtils.isEmpty(json)) {
            return true;
        }
        return TextUtils.equals(json, "{}");
    }
}
