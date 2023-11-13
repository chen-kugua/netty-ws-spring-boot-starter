package com.cpiwx.nettyws.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenPan
 * @date 2023-08-23 18:03
 **/
@Slf4j
public class UserTokenHandlerDefaultImpl extends UserTokenHandler {


    @Override
    public boolean checkToken(String token) {
        return true;
    }


}
