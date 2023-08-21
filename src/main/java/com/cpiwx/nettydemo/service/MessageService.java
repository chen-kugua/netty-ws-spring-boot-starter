package com.cpiwx.nettydemo.service;

import com.cpiwx.nettydemo.model.dto.LoginDTO;

/**
 * @author chenPan
 * @date 2023-08-21 16:22
 **/
public interface MessageService {

    String test();

    String testLogIn(LoginDTO dto);
}
