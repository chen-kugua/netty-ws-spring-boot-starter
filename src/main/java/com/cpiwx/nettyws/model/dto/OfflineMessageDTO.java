package com.cpiwx.nettyws.model.dto;

import com.cpiwx.nettyws.enums.MessageTypeEnum;
import lombok.Data;

/**
 * 离线消息体
 *
 * @author chenPan
 * @date 2023-12-08 10:50
 **/
@Data
public class OfflineMessageDTO {
    /**
     * {@link MessageTypeEnum}
     * MessageTypeEnum#name
     */
    private String type;

    private String fromId;

    private String toId;

    private String content;

    public static OfflineMessageDTO of(MessageDTO dto) {
        OfflineMessageDTO msg = new OfflineMessageDTO();
        msg.setContent(dto.getContent());
        msg.setFromId(dto.getFromId());
        msg.setToId(dto.getToId());
        msg.setType(dto.getType());
        return msg;
    }
}

