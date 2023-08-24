package com.cpiwx.nettyws.utils;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author chenPan
 * @date 2023-08-21 15:21
 **/
public class WsMessageUtil {

    public static TextWebSocketFrame wrapMessage(String message) {
        return new TextWebSocketFrame(message);
    }

    public static boolean isCtxOpen(ChannelHandlerContext ctx) {
        return null != ctx && ctx.channel().isOpen();
    }

    private static void sendMsg(ChannelHandlerContext ctx, String message) {
        TextWebSocketFrame msg = wrapMessage(message);
        ctx.channel().writeAndFlush(msg);
    }

    public static boolean sendMsg(ChannelHandlerContext ctx, Object data) {
        if (!isCtxOpen(ctx)) {
            return false;
        }
        if (null == data) {
            sendMsg(ctx, null);
        } else if (data instanceof String) {
            sendMsg(ctx, (String) data);
        } else if (ParamUtil.isPrimitive(data.getClass())) {
            sendMsg(ctx, data.toString());
        } else {
            sendMsg(ctx, JSONUtil.toJsonStr(data));
        }
        return true;
    }

}
