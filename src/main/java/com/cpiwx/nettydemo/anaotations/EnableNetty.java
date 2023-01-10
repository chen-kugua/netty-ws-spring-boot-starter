package com.cpiwx.nettydemo.anaotations;

import com.cpiwx.nettydemo.conf.NettyServer;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Classname EnableNetty
 * @Description 启用netty-websocket
 * @Date 2023/1/10 17:33
 * @Author chenPan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NettyServer.class)
public @interface EnableNetty {
}
