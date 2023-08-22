package com.cpiwx.nettyws.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.cpiwx.nettyws.model.dto.RequestHandleDTO;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
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
        try {
            // method.invoke(handler.getClassObj(), data);
            if (method.getParameterCount() > 0) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> type = parameterTypes[0];
                return ReflectUtil.invoke(handler.getClassObj(), method, type == String.class ? data : JSONUtil.toBean(data, type));
            } else {
                return ReflectUtil.invoke(handler.getClassObj(), method);
            }
        } catch (Exception e) {
            log.error("反射执行异常", e);
            return null;
        }
    }


}
