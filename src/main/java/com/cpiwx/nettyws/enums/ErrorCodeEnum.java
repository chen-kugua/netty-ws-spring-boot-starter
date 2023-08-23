package com.cpiwx.nettyws.enums;

import lombok.Getter;

/**
 * @author chenPan
 * @date 2023-08-21 14:14
 **/
@Getter
public enum ErrorCodeEnum {

    BODY_ERR("100001", "消息体不是JSON"),
    MESSAGE_TYPE_ERR("100002", "不支持的消息类型"),
    ;

    private final String code;
    private final String details;

    ErrorCodeEnum(String code, String details) {
        this.code = code;
        this.details = details;
    }
}
