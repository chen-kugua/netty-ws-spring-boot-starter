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

    /**
     * 用于IP定位转换
     */
    public static final String REGION = "内网IP|内网IP";
    /**
     * win 系统
     */
    public static final String WIN = "win";

    /**
     * mac 系统
     */
    public static final String MAC = "mac";

    /**
     * 常用接口
     */
    // IP归属地查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";

    public static final String LOG_TABLE_NAME = "sys_log";

    public static final String INIT_SQL_PATH = "sql/sysLogInit.sql";

    public static final String TIMEOUT_WARNING_PREFIX = "TWarning:";
    public static final String ERROR_WARNING_PREFIX = "EWarning:";

    public static final String YES = "1";

    public static final String LOG_TYPE_INFO = "INFO";
    public static final String LOG_TYPE_ERR = "ERROR";

    public static final String MAIL_TYPE_TEXT = "text";

    public static final String MAIL_TYPE_HTML = "html";
}
