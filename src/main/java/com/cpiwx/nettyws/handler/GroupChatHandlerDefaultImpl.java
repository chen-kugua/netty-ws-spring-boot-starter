package com.cpiwx.nettyws.handler;

import cn.hutool.core.collection.CollUtil;
import com.cpiwx.nettyws.model.dto.MessageDTO;
import com.cpiwx.nettyws.model.dto.OfflineMessageDTO;
import com.cpiwx.nettyws.utils.WsMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chenPan
 * @date 2023-08-24 09:34
 **/
@Slf4j
public class GroupChatHandlerDefaultImpl implements GroupChatHandler {
    @Resource
    private UserTokenHandler userTokenService;
    @Resource
    private OfflineMessageHandler offlineMessageHandler;

    @Override
    public void sendMsg(ChannelHandlerContext ctx, MessageDTO dto) {
        List<CopyOnWriteArrayList<ChannelHandlerContext>> allContext = userTokenService.getAllContext();
        boolean sendSuccess;
        if (CollUtil.isNotEmpty(allContext)) {
            for (CopyOnWriteArrayList<ChannelHandlerContext> context : allContext) {
                sendSuccess = false;
                for (ChannelHandlerContext c : context) {
                    sendSuccess = WsMessageUtil.sendMsg(c, dto);
                }
                if (!sendSuccess) {
                    log.debug("发送消息失败，用户离线，保存离线消息");
                    offlineMessageHandler.putMessage(OfflineMessageDTO.of(dto));
                }
            }
        }
    }
}
