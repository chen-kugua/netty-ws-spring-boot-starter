package com.cpiwx.nettydemo.dto;

import lombok.Data;

/**
 * @Classname MessageDto
 * @Description MessageDto
 * @Date 2023/1/10 18:17
 * @Author chenPan
 */
@Data
public class MessageDto {
    private Integer type;

    private String fromId;

    private String toId;

    private String content;
}
