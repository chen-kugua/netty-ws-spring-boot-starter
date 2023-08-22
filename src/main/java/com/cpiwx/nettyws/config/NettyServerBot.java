package com.cpiwx.nettyws.config;

import com.cpiwx.nettyws.properties.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;

/**
 * @Classname NettyServerBot
 * @Description netty服务器启动关闭
 * @Date 2023/1/11 9:33
 * @Author chenPan
 */
@Slf4j
public class NettyServerBot implements CommandLineRunner {
    @Resource
    private NettyProperties nettyProperties;
    @Resource
    private ServerBootstrap serverBootstrap;


    @Override
    public void run(String... args) throws Exception {
        // 绑定端口启动
        log.info("启动Netty服务器=====》");
        serverBootstrap.bind(nettyProperties.getPort()).sync();
        log.info("启动成功，运行端口: {}", nettyProperties.getPort());
    }


}
