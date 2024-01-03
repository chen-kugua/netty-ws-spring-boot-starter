package com.cpiwx.nettyws.model.dto;

import lombok.Data;

/**
 * @author chenPan
 * @date 2023-08-25 17:59
 **/
@Data
public class FeiShuDTO {
    private boolean enable;

    private String hookUrl;

    private String secret;
}
