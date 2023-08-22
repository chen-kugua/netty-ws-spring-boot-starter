package com.cpiwx.nettyws.config;

import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.handler.WebSocketServerHandler;
import com.cpiwx.nettyws.properties.NettyProperties;
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
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;


/**
 * @Classname WebSocketServer
 * @Description WebSocketServer
 * @Date 2022/12/23 17:51
 * @Author chenPan
 */
@Slf4j
// @Configuration 不需要 因为@EnableNetty注解用@Import导入了本类
@EnableConfigurationProperties(NettyProperties.class)
public class NettyAutoConfig {
    @Resource
    private NettyProperties nettyProperties;

    /**
     * boss 线程池
     * 负责客户端连接
     *
     * @return
     */
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(nettyProperties.getBossNum());
    }

    /**
     * worker线程池
     * 负责业务处理
     *
     * @return
     */
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyProperties.getWorkerNum());
    }

    /**
     * 服务器启动器
     *
     * @return
     */
    @Bean
    public ServerBootstrap serverBootstrap(@Qualifier("bossGroup") NioEventLoopGroup bossGroup,
                                           @Qualifier("workerGroup") NioEventLoopGroup workerGroup, WebSocketServerHandler handler) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)   // 指定使用的线程组
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
                        pipeline.addLast(new WebSocketServerCompressionHandler()); // 支持WebSocket压缩
                        // 自定义处理器 需要在WebSocketServerProtocolHandler之前 处理url带参数问题
                        pipeline.addLast(handler);
                        // websocket 处理类
                        pipeline.addLast(new WebSocketServerProtocolHandler(Constants.DEFAULT_WEB_SOCKET_LINK));
                    }
                });
        return serverBootstrap;
    }

    @Bean
    public NettyServerBot nettyServerBot() {
        return new NettyServerBot();
    }
}
