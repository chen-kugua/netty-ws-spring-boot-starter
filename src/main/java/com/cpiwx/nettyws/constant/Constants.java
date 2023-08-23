package com.cpiwx.nettyws.constant;

/**
 * @Classname Constants
 * @Description Constants 常量定义
 * @Date 2022/12/23 18:16
 * @Author chenPan
 */
public class Constants {
    // 客户端连接时就需要 ws://host:port/ws
    // 如果没有末尾的ws路径 会导致请求不走websocket握手一直处于连接中状态 建立连接不成功
    public static final String DEFAULT_WEB_SOCKET_LINK = "/ws";
    public static final String USER_ID = "userId";

    public static final String HEADER_TOKEN = "token";

    public static final String PING = "ping";
    public static final String PONG = "pong";
}
