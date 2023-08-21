package com.cpiwx.nettydemo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author chenPan
 * @date 2023-08-21 16:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestHandleDTO {
    private String className;

    private Object classObj;

    private Method method;


}
