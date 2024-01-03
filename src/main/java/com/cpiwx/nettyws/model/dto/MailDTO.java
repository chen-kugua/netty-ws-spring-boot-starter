package com.cpiwx.nettyws.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author chenPan
 * @date 2023-08-30 11:03
 **/
@Data
public class MailDTO {
    /**
     * 是否启用
     */
    private boolean enable;

    /**
     * 接收人邮箱
     */
    List<String> receivers;


}
