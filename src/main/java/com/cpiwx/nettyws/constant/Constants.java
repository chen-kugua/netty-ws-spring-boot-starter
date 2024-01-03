package com.cpiwx.nettyws.constant;

/**
 * @Classname Constants
 * @Description Constants 常量定义
 * @Date 2022/12/23 18:16
 * @Author chenPan
 */
public class Constants {

    public static String IDENTITY_KEY = "userId";

    public static String CHANNEL_ID = "channelId";

    public static String TOKEN_KEY = "token";

    public static boolean NEED_CHECK_TOKEN = false;

    public static final String PING = "ping";

    public static final String PONG = "pong";

    public static boolean ALLOW_MULTI_CLIENT = false;

    public static final long WORKER_ID = 1L;
    public static final long CENTER_ID = 1L;

    /**
     * 会话消息列表前缀
     */
    public static final String SESSION_LIST_PREFIX = "sessionList:";

    /**
     * 未读数
     */
    public static final String UNREAD_PREDIX = "unread:";
}
