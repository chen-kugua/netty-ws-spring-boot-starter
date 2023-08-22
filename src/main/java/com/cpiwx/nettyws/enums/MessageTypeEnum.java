package com.cpiwx.nettyws.enums;

/**
 * @Classname MessageTypeEnum
 * @Description 消息类型
 * @Date 2023/1/10 17:28
 * @Author chenPan
 */
public enum MessageTypeEnum {
    //定义消息类型
    CONNECT(1, "第一次（或重连）初始化连接"),
    CHAT(2, "聊天消息"),
    SIGNED(3, "消息签收"),
    KEEPALIVE(4, "客户端保持心跳"),
    PULL_FRIEND(5, "拉取好友"),
    API(6, "访问api");

    public final Integer type;
    public final String content;

    MessageTypeEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
