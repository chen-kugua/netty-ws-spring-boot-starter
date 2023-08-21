package com.cpiwx.nettydemo.model;

import com.cpiwx.nettydemo.enums.ErrorCodeEnum;
import lombok.Data;

/**
 * @author chenPan
 * @date 2023-08-21 14:07
 **/
@Data
public class Result<T> {
    private String code;

    private String message;

    private String details;

    private T data;

    public static final String CODE_SUCCESS = "200";
    public static final String MSG_SUCCESS = "操作成功";

    public static final String CODE_FAIL = "9999";
    public static final String MSG_FAIL = "操作异常";

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(CODE_SUCCESS);
        r.setMessage(MSG_SUCCESS);
        r.setData(data);
        return r;
    }

    public static <T> Result<T> fail(String details) {
        Result<T> r = new Result<>();
        r.setCode(CODE_FAIL);
        r.setMessage(MSG_FAIL);
        r.setDetails(details);
        return r;
    }

    public static <T> Result<T> fail(ErrorCodeEnum errorCodeEnum) {
        Result<T> r = new Result<>();
        r.setCode(errorCodeEnum.getCode());
        r.setMessage(MSG_FAIL);
        r.setDetails(errorCodeEnum.getDetails());
        return r;
    }
}
