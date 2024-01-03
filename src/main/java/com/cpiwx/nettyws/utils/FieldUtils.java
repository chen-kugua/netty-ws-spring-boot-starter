package com.cpiwx.nettyws.utils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author chenPan
 * @date 2023-08-31 09:25
 **/
public class FieldUtils {

    @SafeVarargs
    public static <T> List<String> getField(SlFunction<T, ?>... functions) {
        if (functions == null || functions.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(functions).map(FieldUtils::getField).collect(Collectors.toList());
    }

    public static <T> String getField(SlFunction<T, ?> func) {
        try {
            //writeReplace从哪里来的？虚拟机会自动给实现Serializable接口的lambda表达式生成 writeReplace()方法
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            //调用writeReplace()方法，返回一个SerializedLambda对象
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(func);
            //得到lambda表达式中调用的方法名，如 "User::getSex"，则得到的是"getSex"
            String getterMethod = serializedLambda.getImplMethodName();
            //去掉”get"前缀，最终得到字段名“sex"
            return methodToProperty(getterMethod);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else {
            if (!name.startsWith("get") && !name.startsWith("set")) {
                throw new IllegalArgumentException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }

            name = name.substring(3);
        }

        if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

    @SafeVarargs
    public static <T> List<String> getDbField(SlFunction<T, ?>... functions) {
        ArrayList<String> list = new ArrayList<>(functions.length);
        for (SlFunction<T, ?> function : functions) {
            String fieldName = getField(function);
            list.add(StringUtils.toUnderScoreCase(fieldName));
        }
        return list;
    }

}
