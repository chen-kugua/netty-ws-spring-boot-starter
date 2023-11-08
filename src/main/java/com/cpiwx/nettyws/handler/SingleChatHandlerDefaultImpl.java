package com.cpiwx.nettyws.handler;

import com.cpiwx.nettyws.model.dto.MessageDTO;
import com.cpiwx.nettyws.service.UserTokenService;
import com.cpiwx.nettyws.utils.WsMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author chenPan
 * @date 2023-08-23 17:12
 **/
@Slf4j
public class SingleChatHandlerDefaultImpl implements SingleChatHandler {
    @Resource
    private UserTokenService userTokenService;

    @Override
    public void handle(ChannelHandlerContext ctx, MessageDTO dto) {
        String toId = dto.getToId();
        ChannelHandlerContext context = userTokenService.getContext(toId);
        boolean success = WsMessageUtil.sendMsg(context, dto);
        log.debug("{}发送消息发送给【{}】，结果：【{}】", dto.getFromId(), toId, success);
    }

    @Override
    public void sendMessage(MessageDTO dto) {
        String toId = dto.getToId();
        ChannelHandlerContext context = userTokenService.getContext(toId);
        boolean success = WsMessageUtil.sendMsg(context, dto);
        log.debug("{}发送消息发送给【{}】，结果：【{}】", dto.getFromId(), toId, success);
    }
}
