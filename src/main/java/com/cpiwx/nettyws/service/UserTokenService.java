package com.cpiwx.nettyws.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;

/**
 * @author chenPan
 * @date 2023-08-22 16:27
 **/
public interface UserTokenService {

    boolean checkToken(CharSequence token);

    void putContext(String identity, ChannelHandlerContext ctx);

    ChannelHandlerContext getContext(String identity);

    Collection<ChannelHandlerContext> getAllContext();

    void removeContext(String identity);

}
