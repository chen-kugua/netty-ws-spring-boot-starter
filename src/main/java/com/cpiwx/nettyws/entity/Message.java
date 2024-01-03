package com.cpiwx.nettyws.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName t_message
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    /**
     * 消息ID
     */
    private Long id;

    /**
     * 发送者ID
     */
    private String fromId;

    /**
     * 接受者ID
     */
    private String toId;

    /**
     * 消息类型MessageTypeEnum 1单聊 2群聊
     */
    private String msgType;

    /**
     * 文本类型 ContentTypeEnum
     */
    private String contentType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 已读标记
     */
    private Integer readFlag;

    /**
     * 创建时间
     */
    private Date crtTime;

    private static final long serialVersionUID = 1L;


}
