package com.cpiwx.nettydemo.conf;

import com.cpiwx.nettydemo.properties.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Classname NettyServer
 * @Description netty 服务端
 * @Date 2023/1/10 17:35
 * @Author chenPan
 */
@Slf4j
@RequiredArgsConstructor
public class NettyServer {
    private final ServerBootstrap serverBootstrap;
    private final NioEventLoopGroup boosGroup;
    private final NioEventLoopGroup workerGroup;
    private final NettyProperties nettyProperties;

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
