package com.cpiwx.nettyws.properties;

import com.cpiwx.nettyws.constant.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * @Classname NettyProperties
 * @Description netty配置
 * @Date 2023/1/10 17:18
 * @Author chenPan
 */
@ConfigurationProperties(prefix = "netty.ws")
@Data
public class NettyProperties {
    /**
     * boss线程数量
     * 这是用于接收客户端连接的线程组。它通常会处理客户端的连接请求，然后将已连接的客户端传递给 workerGroup 进行后续的处理。
     * 在通常的情况下，bossGroup 会指定一个单线程的 EventLoopGroup，它监听传入的连接并将连接分配给 workerGroup 的线程。
     * Netty boss 线程池是处理 accept事件的，不管线程池多大，只会使用一个线程，既然只使用一个线程为什么要用线程池呢？
     * 主要是异常的情况下，线程die了，可以再创建一个新线程，那什么情况下boss线程池可以使用多个线程呢？
     * 那就是当ServerBootstrap bind多个端口时。每个端口都有一个线程eventLoop accept事件。
     */
    private Integer bossNum = 1;

    /**
     * worker线程数量
     * 这是用于处理已连接客户端请求的线程组。一旦客户端的连接被 bossGroup 接受，它会将连接交给 workerGroup 进行处理。
     * workerGroup 可以包含多个线程，通常用于执行实际的业务逻辑和 I/O 操作。
     * 为0 netty则会设置为cpu核心数*2的数量
     */
    private Integer workerNum = 0;

    /**
     * 服务器主端口 默认9000
     * 可以bind多个端口 多个端口时bossNum>1有效
     */
    private String port = "9000";

    /**
     * 连接超时时间 默认为30s
     */
    private Integer timeout = 30000;

    /**
     * 是否校验token
     */
    private boolean needCheckToken = false;

    /**
     * ws端点路径
     * ws://host:port/{endpoint}
     */
    private String endpoint = "/ws";

    /**
     * URL中token名称
     */
    private String tokenKey = "token";

    /**
     * URL中身份id名称
     */
    private String identityKey = "userId";

    /**
     * ws最大帧
     *  默认65536 客户端传递比较大的对象时，maxFrameSize参数的值需要调大
     */
    private Integer maxFrameSize = 65536;

    /**
     * 是否允许通一个客户端ID（userId）建立不同的连接
     */
    private boolean allowMultiClient;

    /**
     * 开启服务端ping 维护心跳
     */
    private boolean enableServerPing;

    /**
     * 秒 隔多长时间发送ping
     */
    private Integer timeInterval = 15;

    @PostConstruct
    public void init() {
        Constants.IDENTITY_KEY = identityKey;
        Constants.TOKEN_KEY = tokenKey;
        Constants.NEED_CHECK_TOKEN = needCheckToken;
        Constants.ALLOW_MULTI_CLIENT = allowMultiClient;
    }
}
