package com.cpiwx.nettyws.service.impl;

import cn.hutool.core.util.IdUtil;
import com.cpiwx.nettyws.model.dto.LoginDTO;
import com.cpiwx.nettyws.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author chenPan
 * @date 2023-08-21 16:22
 **/
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    @Override
    public String test() {
        String id = IdUtil.fastUUID();
        log.info("request test：{}", id);
        return id;
    }

    @Override
    public String testLogIn(LoginDTO dto) {
        log.info("testLogin,{},{}", dto.getUserId(), dto.getPassword());
        return IdUtil.simpleUUID();
    }
}
