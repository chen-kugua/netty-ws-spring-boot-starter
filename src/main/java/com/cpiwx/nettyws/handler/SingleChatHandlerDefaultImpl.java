package com.cpiwx.nettyws.handler;

import com.cpiwx.nettyws.model.dto.MessageDTO;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenPan
 * @date 2023-08-23 17:12
 **/
@Slf4j
public class SingleChatHandlerDefaultImpl implements SingleChatHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, MessageDTO dto) {

    }
}
