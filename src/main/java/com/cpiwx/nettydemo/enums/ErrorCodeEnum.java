package com.cpiwx.nettydemo.enums;

import lombok.Getter;

/**
 * @author chenPan
 * @date 2023-08-21 14:14
 **/
@Getter
public enum ErrorCodeEnum {

    body_err("100001", "消息体不是JSON");

    private String code;
    private String details;

    ErrorCodeEnum(String code, String details) {

    }
}
