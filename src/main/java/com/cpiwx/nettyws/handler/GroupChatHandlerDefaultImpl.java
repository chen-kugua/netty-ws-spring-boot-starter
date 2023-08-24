package com.cpiwx.nettyws.handler;

import cn.hutool.core.collection.CollUtil;
import com.cpiwx.nettyws.model.dto.MessageDTO;
import com.cpiwx.nettyws.service.UserTokenService;
import com.cpiwx.nettyws.utils.WsMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author chenPan
 * @date 2023-08-24 09:34
 **/
@Slf4j
public class GroupChatHandlerDefaultImpl implements GroupChatHandler {
    @Resource
    private UserTokenService userTokenService;

    @Override
    public void handle(ChannelHandlerContext ctx, MessageDTO dto) {
        Collection<ChannelHandlerContext> allContext = userTokenService.getAllContext();
        if (CollUtil.isNotEmpty(allContext)) {
            for (ChannelHandlerContext context : allContext) {
                WsMessageUtil.sendMsg(context, dto);
            }
        }
    }
}
