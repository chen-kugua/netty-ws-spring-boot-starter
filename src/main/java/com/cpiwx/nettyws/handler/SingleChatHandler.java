package com.cpiwx.nettyws.handler;

import com.cpiwx.nettyws.model.dto.MessageDTO;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenPan
 * @date 2023-08-22 17:04
 **/
public interface SingleChatHandler {

    void handle(ChannelHandlerContext ctx, MessageDTO dto);

    /**
     * 发送消息
     * @param dto 消息
     */
    void sendMessage(MessageDTO dto);


}
