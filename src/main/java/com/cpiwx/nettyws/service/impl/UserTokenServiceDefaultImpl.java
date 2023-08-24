package com.cpiwx.nettyws.service.impl;

import com.cpiwx.nettyws.service.UserTokenService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenPan
 * @date 2023-08-23 18:03
 **/
@Slf4j
public class UserTokenServiceDefaultImpl implements UserTokenService {
    private final ConcurrentHashMap<String, ChannelHandlerContext> clients = new ConcurrentHashMap<>();

    @Override
    public boolean checkToken(CharSequence token) {
        return true;
    }

    @Override
    public void putContext(String identity, ChannelHandlerContext ctx) {
        clients.put(identity, ctx);
    }

    @Override
    public ChannelHandlerContext getContext(String identity) {
        return clients.get(identity);
    }

    @Override
    public Collection<ChannelHandlerContext> getAllContext() {
        return clients.values();
    }

    @Override
    public void removeContext(String identity) {
        clients.remove(identity);
    }
}
