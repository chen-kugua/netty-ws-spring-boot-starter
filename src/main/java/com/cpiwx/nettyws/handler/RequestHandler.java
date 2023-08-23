package com.cpiwx.nettyws.handler;

import com.cpiwx.nettyws.anaotations.Request;
import com.cpiwx.nettyws.anaotations.WsController;
import com.cpiwx.nettyws.utils.RequestMappingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

/**
 * @author chenPan
 * @date 2023-08-21 16:18
 * @descrition 启动时扫描bean中包含WsController注解的类 将其 @Request注解标注的方法加入路径匹配
 **/
@Slf4j
public class RequestHandler implements BeanPostProcessor {

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
