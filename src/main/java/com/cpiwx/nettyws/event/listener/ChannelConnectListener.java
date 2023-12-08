package com.cpiwx.nettyws.event.listener;

import cn.hutool.core.collection.CollUtil;
import com.cpiwx.nettyws.event.ChannelConnectEvent;
import com.cpiwx.nettyws.handler.OfflineMessageHandler;
import com.cpiwx.nettyws.model.dto.OfflineMessageDTO;
import com.cpiwx.nettyws.utils.WsMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chenPan
 * @date 2023-12-08 14:25
 **/
@Slf4j
@Service
public class ChannelConnectListener implements ApplicationListener<ChannelConnectEvent> {
    @Resource
    private OfflineMessageHandler offlineMessageHandler;

    /**
     * 默认是同步方法 发送端需要等监听这处理完才会返回
     *
     * @param event 事件
     */
    @Override
    @Async("offlineMessageExecutor")
    public void onApplicationEvent(ChannelConnectEvent event) {
        log.debug("ChannelConnectListener 监听到连接事件");
        WsMessageUtil.sendMsg(event.getCtx(), "连接成功");
        List<OfflineMessageDTO> messages = offlineMessageHandler.getMessageAndClear(event.getClientId());
        if (CollUtil.isNotEmpty(messages)) {
            log.debug("发送离线消息...");
            for (OfflineMessageDTO message : messages) {
                WsMessageUtil.sendMsg(event.getCtx(), message);
            }
        }
    }


    // condition属性可以根据事件对象是否满足条件表达式来过滤事件。
    // @EventListener(classes={ChannelConnectEvent.class}, condition="#event.clientId==1")
    // public void process(ChannelConnectEvent event) {
    //     // 事件处理
    // }
}
