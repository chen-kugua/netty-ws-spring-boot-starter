package com.cpiwx.nettyws.controller;

import com.cpiwx.nettyws.anaotations.Param;
import com.cpiwx.nettyws.anaotations.Request;
import com.cpiwx.nettyws.anaotations.WsController;
import com.cpiwx.nettyws.model.Result;
import com.cpiwx.nettyws.model.dto.LoginDTO;
import com.cpiwx.nettyws.service.TestMessageService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author chenPan
 * @date 2023-08-21 16:21
 **/
@WsController
@Slf4j
public class MessageController {
    @Resource
    private TestMessageService messageService;

    @Request("/test/id")
    public Result<String> testId() {
        return Result.ok(messageService.test());
    }

    @Request("/test/login")
    public Result<String> testLogin(LoginDTO dto) {
        return Result.ok(messageService.testLogIn(dto));
    }

    @Request("/test/params")
    public Result<String> testParams(String userId, @Param("loginDto") LoginDTO dto,Integer integer) {
        log.info("userId:{}，dto:{}，int:{}", userId, dto,integer);
        return Result.ok("ok");
    }

}
