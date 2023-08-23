package com.cpiwx.nettyws.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.cpiwx.nettyws.anaotations.Body;
import com.cpiwx.nettyws.anaotations.Param;
import com.cpiwx.nettyws.model.dto.RequestHandleDTO;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenPan
 * @date 2023-08-21 16:30
 **/
@Slf4j
public class RequestMappingUtil {
    private static final Map<String, RequestHandleDTO> mappings = new HashMap<>();

    public static void put(String path, Method method, Object classObj, String className) {
        mappings.put(path, new RequestHandleDTO(className, classObj, method));
    }

    public static RequestHandleDTO getHandler(String path) {
        RequestHandleDTO handler = mappings.get(path);
        Assert.notNull(handler, "NO MAPPINGS for " + path);
        return handler;
    }

    public static Object handle(String path, String data) {
        RequestHandleDTO handler = getHandler(path);
        Method method = handler.getMethod();
        if (method.getParameterCount() > 0) {
            // 有参数方法处理
            ParamUtil paramUtil = new ParamUtil(data);
            Parameter[] parameters = method.getParameters();
            if (!paramUtil.isJson() && parameters.length > 1) {
                throw new IllegalArgumentException("参数转换异常");
            }
            Class<?>[] types = method.getParameterTypes();
            Object[] params = new Object[parameters.length];

            for (int i = 0, len = parameters.length; i < len; i++) {
                // 参数
                Parameter p = parameters[i];
                // 参数类型
                Class<?> c = types[i];
                // 有没有被body标注
                boolean isBody = p.isAnnotationPresent(Body.class);
                // 有Param注解标注则取注解值为参数名
                String name = p.isAnnotationPresent(Param.class) ? p.getAnnotation(Param.class).value() : p.getName();
                params[i] = paramUtil.getParam(isBody, name, c);
            }
            return ReflectUtil.invoke(handler.getClassObj(), method, params);
        } else {
            // 无参方法直接调用
            return ReflectUtil.invoke(handler.getClassObj(), method);
        }
    }


}
