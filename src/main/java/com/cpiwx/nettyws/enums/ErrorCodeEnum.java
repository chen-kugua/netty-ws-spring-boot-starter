package com.cpiwx.nettyws.enums;

import lombok.Getter;

/**
 * @author chenPan
 * @date 2023-08-21 14:14
 **/
@Getter
public enum ErrorCodeEnum {

    body_err("100001", "消息体不是JSON");

    private final String code;
    private final String details;

    ErrorCodeEnum(String code, String details) {
        this.code = code;
        this.details = details;
    }
}
