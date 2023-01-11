package com.cpiwx.nettydemo.conf;

import com.cpiwx.nettydemo.properties.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @Classname NettyServerBot
 * @Description TODO
 * @Date 2023/1/11 9:33
 * @Author chenPan
 */
@Slf4j
public class NettyServerBot {
    @Resource
    private NettyProperties nettyProperties;
    @Resource
    private ServerBootstrap serverBootstrap;
    @Resource
    private NioEventLoopGroup boosGroup;
    @Resource
    private NioEventLoopGroup workerGroup;

    /**
     * 开机启动
     *
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
        // 绑定端口启动
        serverBootstrap.bind(nettyProperties.getPort()).sync();
        // 备用端口
        // serverBootstrap.bind(nettyProperties.getPortSalve()).sync();
        log.info("启动Netty服务器，运行端口: {}", nettyProperties.getPort());
    }

    /**
     * 关闭线程池
     */
    @PreDestroy
    public void close() {
        log.info("关闭Netty服务器");
        boosGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
