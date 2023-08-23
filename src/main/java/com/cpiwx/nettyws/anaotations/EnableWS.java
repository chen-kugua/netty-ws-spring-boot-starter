package com.cpiwx.nettyws.anaotations;

import com.cpiwx.nettyws.config.NettyAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname EnableWS
 * @Description 启用netty-websocket
 * @Date 2023/1/10 17:33
 * @Author chenPan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({NettyAutoConfig.class})
public @interface EnableWS {
}
