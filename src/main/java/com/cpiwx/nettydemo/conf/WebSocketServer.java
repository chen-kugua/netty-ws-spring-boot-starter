package com.cpiwx.nettydemo.conf;

import com.cpiwx.nettydemo.constant.Constants;
import com.cpiwx.nettydemo.handler.WebSocketServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * @Classname WebSocketServer
 * @Description WebSocketServer
 * @Date 2022/12/23 17:51
 * @Author chenPan
 */
@Component
public class WebSocketServer {

    @PostConstruct
    public void startServer() {
        Thread thread = new Thread(
                () -> {
                    try {
                        run(8888);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("======netty启动失败=====");
                    }
                }
        );
        thread.setDaemon(true);
        thread.start();
    }

    public void run(int port) throws Exception {
        System.out.println("======启动nettyServer=========");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
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
            // 监听端口
            Channel ch = b.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}
