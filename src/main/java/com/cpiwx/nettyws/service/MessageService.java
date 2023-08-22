package com.cpiwx.nettyws.service;

import com.cpiwx.nettyws.model.dto.LoginDTO;

/**
 * @author chenPan
 * @date 2023-08-21 16:22
 **/
public interface MessageService {

    String test();

    String testLogIn(LoginDTO dto);
}
