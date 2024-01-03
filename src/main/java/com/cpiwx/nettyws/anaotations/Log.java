package com.cpiwx.nettyws.anaotations;


import com.cpiwx.nettyws.enums.OperationTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志注解
 *
 * @author chenPan
 * @date 2023-08-25 10:11
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    /**
     * 接口描述
     */
    String value() default "";

    /**
     * 操作类型
     */
    OperationTypeEnum operationType() default OperationTypeEnum.QUERY;

    /**
     * 是否开启超时告警
     */
    boolean timeoutEarlyWarning() default false;

    /**
     * 是否开启接口报错预警
     */
    boolean errorEarlyWarning() default false;

    /**
     * 超过多少毫秒视为需要告警
     */
    int timeThreshold() default 500;

    /**
     * 同一类型预警间隔多少毫秒
     */
    int interval() default 10 * 1000;

}
