package com.cpiwx.nettyws.service;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenPan
 * @date 2023-08-22 16:27
 **/
public interface UserTokenService {

    boolean checkToken(CharSequence token);

    void putContext(String identity, ChannelHandlerContext ctx);

    ChannelHandlerContext getContext(String identity);

    void removeContext(String identity);

}
