package com.cpiwx.nettyws.utils;

import cn.hutool.json.JSONUtil;
import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.model.Result;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenPan
 * @date 2023-08-21 15:21
 **/
public class WsMessageUtil {
    private static ConcurrentHashMap<String, ChannelHandlerContext> onlineContainer = new ConcurrentHashMap<>();

    public static TextWebSocketFrame wrapMessage(String message) {
        return new TextWebSocketFrame(message);
    }

    public static void sendMsg(ChannelHandlerContext ctx, String message) {
        TextWebSocketFrame msg = wrapMessage(message);
        ctx.channel().writeAndFlush(msg);
    }

    public static void sendMsg(ChannelHandlerContext ctx, Object data) {
        if (null == data) {
            sendMsg(ctx, (String) null);
        } else if (data instanceof Result) {
            sendMsg(ctx, (Result<?>) data);
        } else if (data instanceof String) {
            sendMsg(ctx, (String) data);
        } else if (JSONUtil.isJson(data.toString())) {
            sendMsg(ctx, JSONUtil.toJsonStr(data));
        } else {
            sendMsg(ctx, data.toString());
        }
    }

    public static void sendMsg(ChannelHandlerContext ctx, Result<?> message) {
        TextWebSocketFrame msg = wrapMessage(JSONUtil.toJsonStr(message));
        ctx.channel().writeAndFlush(msg);
    }

    public static void putUser(String userId, ChannelHandlerContext ctx) {
        onlineContainer.put(userId, ctx);
    }

    public static void removeUser(ChannelHandlerContext ctx) {
        Object userId = ctx.channel().attr(AttributeKey.valueOf(Constants.USER_ID)).get();
        if (null != userId) {
            onlineContainer.remove(userId.toString());
        }
    }
}
