package com.cpiwx.nettyws.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.cpiwx.nettyws.handler.AuthWebSocketHandler;
import com.cpiwx.nettyws.handler.GroupChatHandler;
import com.cpiwx.nettyws.handler.GroupChatHandlerDefaultImpl;
import com.cpiwx.nettyws.handler.OfflineMessageHandler;
import com.cpiwx.nettyws.handler.OfflineMessageHandlerDefaultImpl;
import com.cpiwx.nettyws.handler.RequestHandler;
import com.cpiwx.nettyws.handler.SingleChatHandler;
import com.cpiwx.nettyws.handler.SingleChatHandlerDefaultImpl;
import com.cpiwx.nettyws.handler.UserTokenHandler;
import com.cpiwx.nettyws.handler.UserTokenHandlerDefaultImpl;
import com.cpiwx.nettyws.handler.WsTextFrameHandler;
import com.cpiwx.nettyws.properties.NettyProperties;
import com.cpiwx.nettyws.service.CustomHandlerService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @Classname WebSocketServer
 * @Description WebSocketServer
 * @Date 2022/12/23 17:51
 * @Author chenPan
 */
@Slf4j
@EnableConfigurationProperties(NettyProperties.class)
@Import({NettyServerBot.class, RequestHandler.class})
@EnableAsync
public class NettyAutoConfig {
    @Resource
    private NettyProperties nettyProperties;

    @Setter(onMethod_ = @Autowired(required = false))
    private CustomHandlerService customHandlerService;

    /**
     * boss 线程池
     * 负责客户端连接
     */
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(nettyProperties.getBossNum(), new DefaultThreadFactory("bossGroup"));
    }

    /**
     * worker线程池
     * 负责业务处理
     */
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyProperties.getWorkerNum(), new DefaultThreadFactory("workerGroup"));
    }

    /**
     * 文本帧处理器
     */
    @Bean
    public WsTextFrameHandler webSocketServerHandler() {
        return new WsTextFrameHandler();
    }

    /**
     * 鉴权和用户状态映射管理
     */
    @Bean
    public AuthWebSocketHandler authWebSocketHandler() {
        return new AuthWebSocketHandler();
    }

    /**
     * 服务器启动器
     */
    @Bean
    public ServerBootstrap serverBootstrap(@Qualifier("bossGroup") NioEventLoopGroup bossGroup,
                                           @Qualifier("workerGroup") NioEventLoopGroup workerGroup,
                                           WsTextFrameHandler wsTextFrameHandler,
                                           AuthWebSocketHandler authWebSocketHandler) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)   // 指定使用的线程组
                .channel(NioServerSocketChannel.class) // 指定使用的通道
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyProperties.getTimeout()) // 指定连接超时时间
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        // HttpRequestDecoder和HttpResponseEncoder的一个组合，针对http协议进行编解码
                        pipeline.addLast(new HttpServerCodec())
                                // 将HttpMessage和HttpContents聚合到一个完成的 FullHttpRequest或FullHttpResponse中,具体是FullHttpRequest对象还是FullHttpResponse对象取决于是请求还是响应
                                // 需要放到HttpServerCodec这个处理器后面
                                .addLast(new HttpObjectAggregator(65536))
                                // 分块向客户端写数据，防止发送大文件时导致内存溢出， channel.write(new ChunkedFile(new File("bigFile.mkv")))
                                .addLast(new ChunkedWriteHandler())
                                // webSocket 数据压缩扩展，当添加这个的时候WebSocketServerProtocolHandler的第三个参数需要设置成true
                                .addLast(new WebSocketServerCompressionHandler())
                                // 聚合 websocket 的数据帧，因为客户端可能分段向服务器端发送数据
                                // https://github.com/netty/netty/issues/1112 https://github.com/netty/netty/pull/1207
                                // maxContentLength 参数表示一个消息的最大长度（字节数）。如果聚合后的消息长度超过了这个值，聚合器会自动关闭连接，并抛出异常。
                                .addLast(new WebSocketFrameAggregator(10 * 1024 * 1024))
                                // url参数获取 校验token和连接状态管理
                                .addLast(authWebSocketHandler)
                                // 表示客户端请求 WebSocket 握手的路径。当客户端请求连接到服务器时，服务器会根据这个路径来判断是否进行 WebSocket 握手处理。
                                // 第四个参数maxFrameSize 默认65536 客户端传递比较大的对象时，maxFrameSize参数的值需要调大
                                .addLast(new WebSocketServerProtocolHandler(nettyProperties.getEndpoint(), null, true, nettyProperties.getMaxFrameSize()))
                                // ws文本消息处理器 在websocket握手之前重写URL url带参数会报错 （校验Token）
                                .addLast(wsTextFrameHandler);
                        // 添加自定义处理器
                        if (null != customHandlerService) {
                            customHandlerService.addHandler(pipeline);
                        }
                    }
                });
        return serverBootstrap;
    }

    /**
     * 默认的权限管理和用户映射
     */
    @Bean
    @ConditionalOnMissingBean(UserTokenHandler.class)
    public UserTokenHandler userTokenService() {
        return new UserTokenHandlerDefaultImpl();
    }

    /**
     * ws单聊处理器
     */
    @Bean
    @ConditionalOnMissingBean(SingleChatHandler.class)
    public SingleChatHandler singleChatHandler() {
        return new SingleChatHandlerDefaultImpl();
    }

    /**
     * 群聊处理器
     */
    @Bean
    @ConditionalOnMissingBean(GroupChatHandler.class)
    public GroupChatHandler groupChatHandler() {
        return new GroupChatHandlerDefaultImpl();
    }

    @Bean
    @ConditionalOnMissingBean(OfflineMessageHandler.class)
    public OfflineMessageHandler offlineMessageHandler() {
        return new OfflineMessageHandlerDefaultImpl();
    }

    @Bean("offlineMessageExecutor")
    public ExecutorService offlineMessageExecutor() {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNamePrefix("offline-message--")
                .build();
        // 无容量队列
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(nettyProperties.getQueueCapacity());
        return new ThreadPoolExecutor(
                nettyProperties.getCorePoolSize(),
                nettyProperties.getMaxPoolSize(),
                nettyProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                queue,
                factory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
