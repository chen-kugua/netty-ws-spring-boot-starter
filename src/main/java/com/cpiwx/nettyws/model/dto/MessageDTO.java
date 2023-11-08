package com.cpiwx.nettyws.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Classname MessageDTO
 * @Description MessageDTO
 * @Date 2023/1/10 18:17
 * @Author chenPan
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class MessageDTO {
    private String type;
    /**
     * 请求地址 当 type为APi的时候必填
     */
    private String apiPath;

    private String fromId;

    private String toId;

    private String content;


}
