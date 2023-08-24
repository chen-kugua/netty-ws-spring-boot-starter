package com.cpiwx.nettyws.enums;

/**
 * @Classname MessageTypeEnum
 * @Description 消息类型
 * @Date 2023/1/10 17:28
 * @Author chenPan
 */
public enum MessageTypeEnum {
    //定义消息类型
    API(1, "访问api"),
    SINGLE_CHAT(2, "单聊"),
    GROUP_CHAT(3, "群聊");

    public final Integer code;
    public final String content;

    MessageTypeEnum(Integer code, String content) {
        this.code = code;
        this.content = content;
    }
}
