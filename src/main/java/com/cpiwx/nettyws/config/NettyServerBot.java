package com.cpiwx.nettyws.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.handler.UserTokenHandler;
import com.cpiwx.nettyws.properties.NettyProperties;
import com.cpiwx.nettyws.utils.WsMessageUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Classname NettyServerBot
 * @Description netty服务器启动关闭
 * @Date 2023/1/11 9:33
 * @Author chenPan
 */
@Slf4j
public class NettyServerBot implements CommandLineRunner {
    @Resource
    private NettyProperties nettyProperties;
    @Resource
    private ServerBootstrap serverBootstrap;
    @Resource
    private UserTokenHandler userTokenHandler;
    private String jobId;

    @Override
    public void run(String... args) throws Exception {
        // 绑定端口启动
        log.info("启动Netty服务器=====》");
        String[] split = nettyProperties.getPort().split(StrUtil.COMMA);
        for (String port : split) {
            serverBootstrap.bind(Integer.parseInt(port)).sync();
            log.info("启动成功，运行端口: {}", port);
        }
        if (nettyProperties.isEnableServerPing()) {
            String cronStr = StrUtil.format("0/{} * * * * ?", nettyProperties.getTimeInterval());
            this.jobId = CronUtil.schedule(cronStr, (Task) () -> {
                List<CopyOnWriteArrayList<ChannelHandlerContext>> list = userTokenHandler.getAllContext();
                if (log.isDebugEnabled()) {
                    log.debug("开始服务端ping：客户端数量{}", userTokenHandler.getClientCount());
                }
                for (CopyOnWriteArrayList<ChannelHandlerContext> cpArray : list) {
                    for (ChannelHandlerContext ctx : cpArray) {
                        WsMessageUtil.sendMsg(ctx, Constants.PING);
                    }
                }
            });
            // 匹配秒部分 否则是分
            CronUtil.setMatchSecond(true);
            // stop后立即结束定时任务 不等待任务执行完毕
            CronUtil.start(true);
            log.info("启用了服务端ping，jobId:{}", this.jobId);
        }
    }

    @PreDestroy
    public void destroy() {
        if (null != this.jobId) {
            log.info("关闭ping 定时任务");
            CronUtil.stop();
        }
    }

}
