package com.cpiwx.nettyws.config;

import com.cpiwx.nettyws.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;

/**
 * @author chenPan
 * @date 2023-08-25 13:57
 **/
@Slf4j
public class InitTask implements CommandLineRunner {
    @Resource
    private LogService logService;

    @Override
    public void run(String... args) throws Exception {
        log.info("启动检查日志表");
        logService.initTable();
    }
}
