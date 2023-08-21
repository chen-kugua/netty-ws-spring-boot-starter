package com.cpiwx.nettydemo.conf;

import com.cpiwx.nettydemo.anaotations.Request;
import com.cpiwx.nettydemo.anaotations.WsController;
import com.cpiwx.nettydemo.utils.RequestMappingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author chenPan
 * @date 2023-08-21 16:18
 **/
@Component
@Slf4j
public class RequestHandlerConfig implements BeanPostProcessor {

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(WsController.class)) {
            WsController controller = clazz.getAnnotation(WsController.class);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Request.class)) {
                    Request request = method.getAnnotation(Request.class);
                    String path = controller.value() + request.value();
                    log.info("mapping：{}，{}", path, clazz.getName());
                    RequestMappingUtil.put(path, method, bean, clazz.getName());
                }
            }
        }
        return bean;
    }
}
