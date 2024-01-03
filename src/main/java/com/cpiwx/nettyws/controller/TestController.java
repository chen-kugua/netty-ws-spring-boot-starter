package com.cpiwx.nettyws.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.cpiwx.nettyws.anaotations.Log;
import com.cpiwx.nettyws.enums.OperationTypeEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenPan
 * @date 2023-08-25 14:23
 **/
@RestController
public class TestController {

    @GetMapping("/query")
    @Log("测试查询接口")
    public String query() {
        return "query";
    }

    @GetMapping("/save")
    @Log(value = "测试新增接口",operationType = OperationTypeEnum.SAVE)
    public String save() {
        return "save";
    }

    @GetMapping("/update")
    @Log(value = "测试修改接口",operationType = OperationTypeEnum.UPDATE)
    public String update() {
        return "update";
    }

    @GetMapping("/delete")
    @Log(value = "测试删除接口",operationType = OperationTypeEnum.DELETE)
    public String delete() {
        return "delete";
    }

    @GetMapping("/err")
    @Log(value = "测试接口异常",operationType = OperationTypeEnum.QUERY,errorEarlyWarning = true)
    public String err() {
        int i = 1 / 0;
        return "err";
    }

    @GetMapping("/delay")
    @Log(value = "测试接口delay",operationType = OperationTypeEnum.QUERY,timeoutEarlyWarning = true)
    public String delay() {
        ThreadUtil.sleep(1000);
        return "delay";
    }

}
