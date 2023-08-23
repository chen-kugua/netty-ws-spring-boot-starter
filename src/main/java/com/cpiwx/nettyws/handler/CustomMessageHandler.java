package com.cpiwx.nettyws.handler;

import com.cpiwx.nettyws.model.dto.MessageDTO;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenPan
 * @date 2023-08-23 17:00
 **/
public interface CustomMessageHandler {

    void handle(ChannelHandlerContext ctx, MessageDTO dto);

}
