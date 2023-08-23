package com.cpiwx.nettyws.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenPan
 * @date 2023-08-21 13:59
 **/
@Slf4j
public class ChannelAttrUtil {

    public static void setAttr(ChannelHandlerContext ctx, String key, Object value) {
        ctx.channel().attr(AttributeKey.valueOf(key)).set(value);
    }

    public static <T> T getAttr(ChannelHandlerContext ctx, String key, Class<T> tClass) {
        Object value = ctx.channel().attr(AttributeKey.valueOf(key)).get();
        if (null != value) {
            return tClass.cast(value);
        }
        return null;
    }
}
