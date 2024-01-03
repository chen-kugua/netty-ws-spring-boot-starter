package com.cpiwx.nettyws.service;

import com.cpiwx.nettyws.entity.LogEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;

/**
 * @author jinjin
 * @date 2020-09-27
 */
public interface LogService  {

    /**
     * 保存日志数据
     *
     * @param userId  用户
     * @param browser   浏览器
     * @param ip        请求IP
     * @param joinPoint /
     * @param log       日志实体
     */
    @Async
    void save(String userId, String browser, String ip, ProceedingJoinPoint joinPoint, LogEntity log);

    void initTable();

}
