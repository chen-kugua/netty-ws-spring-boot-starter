package com.cpiwx.nettyws.event;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author chenPan
 * @date 2023-12-08 14:20
 **/
@Getter
@Setter
public class ChannelConnectEvent extends ApplicationEvent {
    /**
     * 客户端ID
     */
    private String clientId;

    private ChannelHandlerContext ctx;

    public ChannelConnectEvent(Object source, ChannelHandlerContext ctx, String clientId) {
        super(source);
        this.clientId = clientId;
        this.ctx = ctx;
    }
}
