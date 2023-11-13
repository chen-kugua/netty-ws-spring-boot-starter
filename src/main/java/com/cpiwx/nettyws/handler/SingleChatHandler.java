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
     * @param ctx 当前通道对象 可为null
     * @param dto 消息体
     */
    void sendMsg(ChannelHandlerContext ctx, MessageDTO dto);

}
