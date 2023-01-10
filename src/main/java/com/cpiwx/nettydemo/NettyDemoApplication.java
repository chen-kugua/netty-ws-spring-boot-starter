package com.cpiwx.nettydemo;

import com.cpiwx.nettydemo.anaotations.EnableNetty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 掘金 参考文档 netty-im https://juejin.cn/post/7155097684901101599
 */
@SpringBootApplication
@EnableNetty
public class NettyDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyDemoApplication.class, args);
    }

}
