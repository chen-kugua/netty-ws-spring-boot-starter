package com.cpiwx.nettyws.service;

import com.cpiwx.nettyws.model.dto.MessageDTO;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenPan
 * @date 2023-08-22 17:04
 **/
public interface MessageHandler {

    void handle(ChannelHandlerContext ctx, MessageDTO dto);
}
