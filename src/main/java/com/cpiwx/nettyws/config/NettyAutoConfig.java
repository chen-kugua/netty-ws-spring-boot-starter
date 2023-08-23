package com.cpiwx.nettyws.config;

import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.handler.RequestHandler;
import com.cpiwx.nettyws.handler.WebSocketServerHandler;
import com.cpiwx.nettyws.properties.NettyProperties;
import com.cpiwx.nettyws.service.UserTokenService;
import com.cpiwx.nettyws.service.UserTokenServiceDefaultImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

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
@Import({NettyServerBot.class, RequestHandler.class})
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
        return new NioEventLoopGroup(nettyProperties.getBossNum(), new DefaultThreadFactory("bossGroup"));
    }

    /**
     * worker线程池
     * 负责业务处理
     *
     * @return
     */
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyProperties.getWorkerNum(), new DefaultThreadFactory("workerGroup"));
    }

    @Bean
    public WebSocketServerHandler webSocketServerHandler() {
        return new WebSocketServerHandler();
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
                        pipeline.addLast(
                                new HttpServerCodec());
                        //  负责将 Http 的一些信息例如版本
                        // 和 Http 的内容继承一个 FullHttpRequesst
                        pipeline.addLast(
                                new HttpObjectAggregator(65536));
                        // 大文件写入的类
                        pipeline.addLast(new ChunkedWriteHandler());
                        // 支持WebSocket压缩
                        pipeline.addLast(new WebSocketServerCompressionHandler());
                        // 自定义处理器 在websocket握手之前重写URL url带参数会报错 （校验Token）
                        pipeline.addLast(handler);
                        // websocket 处理类
                        // 构造参数的意思
                        // 表示客户端请求 WebSocket 握手的路径。当客户端请求连接到服务器时，服务器会根据这个路径来判断是否进行 WebSocket 握手处理。
                        pipeline.addLast(new WebSocketServerProtocolHandler(nettyProperties.getEndpoint()));
                    }
                });
        return serverBootstrap;
    }

    @Bean
    @ConditionalOnMissingBean(UserTokenService.class)
    public UserTokenService userTokenService() {
        return new UserTokenServiceDefaultImpl();
    }
}
