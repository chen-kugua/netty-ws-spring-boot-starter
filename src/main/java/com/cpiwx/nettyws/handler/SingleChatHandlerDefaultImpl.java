package com.cpiwx.nettyws.handler;

import cn.hutool.core.collection.CollUtil;
import com.cpiwx.nettyws.model.dto.MessageDTO;
import com.cpiwx.nettyws.service.MessageService;
import com.cpiwx.nettyws.utils.WsMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chenPan
 * @date 2023-08-23 17:12
 **/
@Slf4j
public class SingleChatHandlerDefaultImpl implements SingleChatHandler {
    @Resource
    private UserTokenHandler userTokenService;
    @Resource
    private OfflineMessageHandler offlineMessageHandler;
    @Resource
    private MessageService messageService;

    @Override
    public boolean sendMsg(ChannelHandlerContext ctx, MessageDTO dto) {
        String toId = dto.getToId();
        // 保存消息 异步
        messageService.saveMessage(dto);
        // 更新未读数
        messageService.updateUnread(dto.getFromId(), toId);
        CopyOnWriteArrayList<ChannelHandlerContext> channels = userTokenService.getContextBatch(toId);
        boolean success = false;
        if (CollUtil.isNotEmpty(channels)) {
            for (ChannelHandlerContext channel : channels) {
                boolean b = WsMessageUtil.sendMsg(channel, dto);
                if (b) {
                    success = true;
                }
            }
        }
        // if (!success) {
        //     log.debug("发送消息失败，用户离线，保存离线消息");
        //     offlineMessageHandler.putMessage(OfflineMessageDTO.of(dto));
        // }
        return success;
    }

}
