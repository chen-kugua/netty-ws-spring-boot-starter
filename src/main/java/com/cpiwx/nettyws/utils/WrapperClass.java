package com.cpiwx.nettyws.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenPan
 * @date 2023-08-31 11:22
 **/
public class WrapperClass<T> {

    private Class<T> entityClass;

    public WrapperClass() {
        this.entityClass = this.extractEntityClass();
    }

    @SuppressWarnings("unchecked")
    private Class<T> extractEntityClass() {
        // 通过反射和泛型参数获取实际类型
        Type type = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
        Class<? extends Type> aClass = actualTypeArgument.getClass();
        return null;
        // return (Class<T>) getSuperClassGenricType(getClass(), 0);
    }

    public List<String> getFields() {
        Field[] declaredFields = this.entityClass.getDeclaredFields();
        return Arrays.stream(declaredFields).map(Field::getName).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {

        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }


}
