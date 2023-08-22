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
    API(2, "访问api");

    public final Integer type;
    public final String content;

    MessageTypeEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
