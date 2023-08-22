package com.cpiwx.nettyws.controller;

import com.cpiwx.nettyws.anaotations.Request;
import com.cpiwx.nettyws.anaotations.WsController;
import com.cpiwx.nettyws.model.Result;
import com.cpiwx.nettyws.model.dto.LoginDTO;
import com.cpiwx.nettyws.service.MessageService;

import javax.annotation.Resource;

/**
 * @author chenPan
 * @date 2023-08-21 16:21
 **/
@WsController
public class MessageController {
    @Resource
    private MessageService messageService;

    @Request("/test/id")
    public Result<String> testId() {
        return Result.ok(messageService.test());
    }

    @Request("/test/params")
    public Result<String> testParams(LoginDTO dto) {
        return Result.ok(messageService.testLogIn(dto));
    }

}
