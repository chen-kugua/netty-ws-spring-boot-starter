package com.cpiwx.nettydemo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
    private Integer bossNum = 2;

    /**
     * worker线程数量
     */
    private Integer workerNum = 2;

    /**
     * 服务器主端口 默认9000
     */
    private Integer port = 9000;

    /**
     * 连接超时时间 默认为30s
     */
    private Integer timeout = 30000;
}
