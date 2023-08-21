package com.cpiwx.nettydemo.controller;

import cn.hutool.core.util.IdUtil;
import com.cpiwx.nettydemo.utils.TokenUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname TestController
 * @Description TestController
 * @Date 2023/1/13 15:14
 * @Author chenPan
 */
@RestController
public class TestController {
    @GetMapping("/login")
    public String test(String userId, String password) {
        String token = IdUtil.simpleUUID();
        TokenUtil.putUser(token, userId);
        return token;
    }


}
