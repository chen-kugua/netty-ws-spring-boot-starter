package com.cpiwx.nettyws.handler;

import com.cpiwx.nettyws.model.dto.MessageDTO;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenPan
 * @date 2023-08-22 17:04
 **/
public interface SingleChatHandler {
    /**
     * 发送消息
     * @param ctx 当前通道对象 可为空
     * @param dto 消息对象
     */
    boolean sendMsg(ChannelHandlerContext ctx, MessageDTO dto);

}
