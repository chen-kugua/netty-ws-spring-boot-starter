package com.cpiwx.nettyws.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenPan
 * @date 2023-08-23 14:50
 **/
@Getter
@Setter
public class ParamUtil {

    private String data;

    private boolean isJson;

    private JSONObject jsonObject;

    private static final List<Class<?>> primitive = Arrays.asList(
            Integer.class, int.class, Long.class, long.class, Short.class, short.class, Byte.class, byte.class,
            Boolean.class, boolean.class, Float.class, float.class, Double.class, double.class, String.class
    );

    public ParamUtil(String data) {
        this.data = data;
        this.isJson = JSONUtil.isJson(data);
        if (this.isJson) {
            this.jsonObject = JSONUtil.parseObj(data);
        }
    }

    public <T> T getParam(String paramName, Class<T> tClass) {
        if (this.isJson) {
            if (ParamUtil.isObj(tClass)) {
                String str = jsonObject.getStr(paramName);
                return JSONUtil.toBean(str, tClass);
            }
            if (tClass == String.class) {
                return tClass.cast(jsonObject.getStr(paramName));
            } else if (tClass == Integer.class || tClass == int.class) {
                return tClass.cast(jsonObject.getInt(paramName));
            } else if (tClass == Long.class || tClass == long.class) {
                return tClass.cast(jsonObject.getLong(paramName));
            } else if (tClass == Short.class || tClass == short.class) {
                return tClass.cast(jsonObject.getShort(paramName));
            } else if (tClass == Byte.class || tClass == byte.class) {
                return tClass.cast(jsonObject.getByte(paramName));
            } else if (tClass == Double.class || tClass == double.class) {
                return tClass.cast(jsonObject.getDouble(paramName));
            } else if (tClass == Float.class || tClass == float.class) {
                return tClass.cast(jsonObject.getFloat(paramName));
            } else if (tClass == Boolean.class || tClass == boolean.class) {
                return tClass.cast(jsonObject.getBool(paramName));
            }
        } else {
            if (tClass == String.class) {
                return tClass.cast(data);
            } else if (tClass == Integer.class || tClass == int.class) {
                return tClass.cast(Integer.parseInt(data));
            } else if (tClass == Long.class || tClass == long.class) {
                return tClass.cast(Long.parseLong(data));
            } else if (tClass == Short.class || tClass == short.class) {
                return tClass.cast(Short.parseShort(data));
            } else if (tClass == Byte.class || tClass == byte.class) {
                return tClass.cast(Byte.parseByte(data));
            } else if (tClass == Double.class || tClass == double.class) {
                return tClass.cast(Double.parseDouble(data));
            } else if (tClass == Float.class || tClass == float.class) {
                return tClass.cast(Float.parseFloat(data));
            } else if (tClass == Boolean.class || tClass == boolean.class) {
                return tClass.cast(Boolean.parseBoolean(data));
            }
        }
        return null;
    }

    public <T> T getParam(boolean isBody, String paramName, Class<T> tClass) {
        if (isBody) {
            return JSONUtil.toBean(data, tClass);
        } else {
            return getParam(paramName, tClass);
        }
    }

    public static boolean isObj(Class<?> clazz) {
        return !isPrimitive(clazz);
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return primitive.contains(clazz);
    }

    public static Map<String, String> getUriParams(String uri) {
        Map<String, String> map = new HashMap<>();
        if (null != uri && uri.contains("?")) {
            String[] uriArray = uri.split("\\?");
            String params = uriArray[1];
            String[] splits = params.split("&");
            for (String s : splits) {
                String[] split = s.split("=");
                if (split.length == 2) {
                    map.put(split[0], split[1]);
                }
            }
        }
        return map;
    }
}
