package com.cpiwx.nettyws.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
     */
    private Integer bossNum ;

    /**
     * worker线程数量
     */
    private Integer workerNum;

    /**
     * 服务器主端口 默认9000
     */
    private Integer port ;

    /**
     * 连接超时时间 默认为30s
     */
    private Integer timeout;

    /**
     * 是否校验token
     */
    private boolean needCheckToken;

    /**
     * header中token名称
     */
    private String tokenKey;
}
