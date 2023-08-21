package com.cpiwx.nettydemo.controller;

import com.cpiwx.nettydemo.anaotations.Request;
import com.cpiwx.nettydemo.anaotations.WsController;
import com.cpiwx.nettydemo.model.Result;
import com.cpiwx.nettydemo.model.dto.LoginDTO;
import com.cpiwx.nettydemo.service.MessageService;

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
