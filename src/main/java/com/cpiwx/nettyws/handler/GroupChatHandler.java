package com.cpiwx.nettyws.handler;

import com.cpiwx.nettyws.model.dto.MessageDTO;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenPan
 * @date 2023-08-22 17:04
 **/
public interface GroupChatHandler {

    void sendMsg(ChannelHandlerContext ctx, MessageDTO dto);

}
