package com.cpiwx.nettydemo.conf;

import com.cpiwx.nettydemo.constant.Constants;
import com.cpiwx.nettydemo.handler.WebSocketServerHandler;
import com.cpiwx.nettydemo.properties.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


/**
 * @Classname WebSocketServer
 * @Description WebSocketServer
 * @Date 2022/12/23 17:51
 * @Author chenPan
 */
@Configuration
public class NettyConfig {

    @Resource
    private NettyProperties nettyProperties;

    /**
     * boss 线程池
     * 负责客户端连接
     *
     * @return
     */
    @Bean
    public NioEventLoopGroup boosGroup() {
        return new NioEventLoopGroup(nettyProperties.getBossNum());
    }

    /**
     * worker线程池
     * 负责业务处理
     *
     * @return
     */
    @Bean
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyProperties.getWorkerNum());
    }

    /**
     * 服务器启动器
     *
     * @return
     */
    @Bean
    public ServerBootstrap serverBootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boosGroup(), workerGroup())   // 指定使用的线程组
                .channel(NioServerSocketChannel.class) // 指定使用的通道
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyProperties.getTimeout()) // 指定连接超时时间
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // http 的解码器
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("http-codec",
                                new HttpServerCodec());
                        //  负责将 Http 的一些信息例如版本
                        // 和 Http 的内容继承一个 FullHttpRequesst
                        pipeline.addLast("aggregator",
                                new HttpObjectAggregator(65536));
                        // 大文件写入的类
                        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                        // 自定义处理器 需要在WebSocketServerProtocolHandler之前 处理url带参数问题
                        pipeline.addLast(new WebSocketServerHandler());
                        // websocket 处理类
                        pipeline.addLast(new WebSocketServerProtocolHandler(Constants.DEFAULT_WEB_SOCKET_LINK));
                    }
                });
        return serverBootstrap;
    }

}
