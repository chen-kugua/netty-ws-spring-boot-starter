package com.cpiwx.nettydemo.controller;

import cn.hutool.core.util.ObjectUtil;
import com.cpiwx.nettydemo.model.dto.TestDto;
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
    @GetMapping("/test")
    public String test(TestDto dto) {
        return ObjectUtil.isEmpty(dto) ? "empty" : dto.toString();
    }
}
