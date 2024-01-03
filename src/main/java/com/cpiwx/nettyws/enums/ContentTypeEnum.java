package com.cpiwx.nettyws.enums;

import lombok.Getter;

/**
 * 消息类型枚举
 *
 * @author chenPan
 * @date 2023-12-19 09:35
 **/
@Getter
public enum ContentTypeEnum {
    /**
     * 文本
     */
    TEXT,
    /**
     * 图片
     */
    IMAGE,
    /**
     * 语音
     */
    VOICE,
    /**
     * 视频
     */
    VIDEO,
    /**
     * 文件
     */
    FILE,
    /**
     * 已读通知
     */
    READ;

}
