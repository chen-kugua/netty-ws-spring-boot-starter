package com.cpiwx.nettyws.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cpiwx.nettyws.anaotations.Log;
import com.cpiwx.nettyws.entity.LogEntity;
import com.cpiwx.nettyws.model.dto.MailDTO;
import com.cpiwx.nettyws.properties.PushProperties;
import com.cpiwx.nettyws.service.MessageSendService;
import com.cpiwx.nettyws.utils.EmailUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenPan
 * @date 2023-08-30 11:05
 **/
@Slf4j
public class MailMessageSendServiceImpl implements MessageSendService {
    @Resource
    private PushProperties pushProperties;

    @Override
    public void handleTimeoutAlertPush(Log aopLog, LogEntity logEntity) {
        MailDTO mailProperties = pushProperties.getMail();
        if (null == mailProperties || !mailProperties.isEnable()) {
            log.debug("右键告警未启用");
            return;
        }
        Map<String, Object> params = buildParams(aopLog, logEntity);
        EmailUtils.sendHtml("接口耗时过长告警", mailProperties.getReceivers(), "timeout.ftl", params);
    }

    @Override
    public void handleErrorAlertPush(Log aopLog, LogEntity logEntity) {
        MailDTO mailProperties = pushProperties.getMail();
        if (null == mailProperties || !mailProperties.isEnable()) {
            return;
        }
        Map<String, Object> params = buildParams(aopLog, logEntity);
        EmailUtils.sendHtml("接口异常告警", mailProperties.getReceivers(), "error.ftl", params);
    }

    private Map<String, Object> buildParams(Log aopLog, LogEntity logEntity) {
        Map<String, Object> params = new HashMap<>();
        params.put("method", logEntity.getMethod());
        params.put("desc", aopLog.value());
        params.put("speedTime", logEntity.getTime());
        params.put("logId", logEntity.getLogId());
        if (StrUtil.isNotBlank(logEntity.getExceptionDetail())) {
            params.put("err", logEntity.getExceptionDetail());
        }
        return params;
    }
}
