package com.cpiwx.nettyws;

import com.cpiwx.nettyws.anaotations.EnableWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 掘金 参考文档 netty-im https://juejin.cn/post/7155097684901101599
 */
@SpringBootApplication
@EnableWS
@Slf4j
public class NettyDemoApplication {

    public static void main(String[] args) throws InterruptedException {
        //App.class.getResourceAsStream("/com/xqxx/lncms/app.txt")，App这个类可以任意替换成其他类，对结果没有影响，因为都是从根目录开始查找。
        // 如果不加正斜杆/，那么则从该方法的调用者class所在的目录查找。
        // 用法为：App.class.getResourceAsStream("app.txt")//App.java和app.txt在同一个目录，App这个类不能随意替换成其他类，
        // 只能替换成同一个包下的类，不然找不到文件。
        // InputStream inputStream = NettyDemoApplication.class.getResourceAsStream("/application.properties");
        SpringApplication.run(NettyDemoApplication.class, args);
        // 不使用web-starter 项目启动后会退出
        // 保持项目不退出
        Thread.currentThread().join();
    }


}
