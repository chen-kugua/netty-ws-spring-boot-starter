package com.cpiwx.nettyws.handler;

import cn.hutool.core.collection.CollUtil;
import com.cpiwx.nettyws.constant.Constants;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chenPan
 * @date 2023-08-22 16:27
 **/
public abstract class UserTokenHandler {
    protected ConcurrentHashMap<String, CopyOnWriteArrayList<ChannelHandlerContext>> clients = new ConcurrentHashMap<>();

    /**
     * 校验token
     *
     * @param token token
     * @return 是否合法token
     */
    public abstract boolean checkToken(String token);

    /**
     * 将连接加入客户端连接池管理
     *
     * @param identity 客户端
     * @param ctx      当前通道
     */
    public void putContext(String identity, ChannelHandlerContext ctx) {
        clearOldClients(identity);
        CopyOnWriteArrayList<ChannelHandlerContext> channels = clients.get(identity);
        if (null == channels) {
            channels = new CopyOnWriteArrayList<>();
            channels.add(ctx);
            clients.put(identity, channels);
        } else {
            channels.add(ctx);
        }
    }

    /**
     * 获取指定ID的通道连接
     *
     * @param identity userId
     * @return ChannelHandlerContext
     */
    public ChannelHandlerContext getContext(String identity) {
        if (null == identity) {
            return null;
        }
        CopyOnWriteArrayList<ChannelHandlerContext> channels = clients.get(identity);
        if (CollUtil.isEmpty(channels)) {
            return null;
        }
        return channels.get(0);
    }

    /**
     * 获取指定客户ID的所有连接（如果允许同ID多端登录）
     *
     * @param identity 客户ID userId
     * @return CopyOnWriteArrayList<ChannelHandlerContext>
     */
    public CopyOnWriteArrayList<ChannelHandlerContext> getContextBatch(String identity) {
        return clients.get(identity);
    }

    /**
     * 获取全部的通道连接
     *
     * @return List<CopyOnWriteArrayList < ChannelHandlerContext>>
     */
    public List<CopyOnWriteArrayList<ChannelHandlerContext>> getAllContext() {
        return (List<CopyOnWriteArrayList<ChannelHandlerContext>>) clients.values();
    }

    /**
     * 移除连接
     *
     * @param identity 客户ID
     * @param ctx      当前连接
     */
    public void removeContext(String identity, ChannelHandlerContext ctx) {
        CopyOnWriteArrayList<ChannelHandlerContext> channels = clients.get(identity);
        if (null == channels) {
            return;
        }
        // 移除全部
        if (null == ctx) {
            for (ChannelHandlerContext channel : channels) {
                closeChannel(channel);
            }
            clients.remove(identity);
            return;
        }

        Iterator<ChannelHandlerContext> it = channels.iterator();
        while (it.hasNext()) {
            ChannelHandlerContext channel = it.next();
            if (channel == ctx) {
                closeChannel(channel);
                it.remove();
                break;
            }
        }
    }

    /**
     * 不允许同ID 多端登录时 建立连接时断开旧的连接
     *
     * @param identity 客户端唯一ID （userId）
     */
    protected void clearOldClients(String identity) {
        if (Constants.ALLOW_MULTI_CLIENT) {
            return;
        }
        CopyOnWriteArrayList<ChannelHandlerContext> channels = clients.get(identity);
        if (null == channels) {
            return;
        }
        for (ChannelHandlerContext channel : channels) {
            closeChannel(channel);
        }
        clients.remove(identity);
    }

    protected void closeChannel(ChannelHandlerContext channel) {
        try {
            channel.close();
        } catch (Exception ignored) {
        }
    }
}
