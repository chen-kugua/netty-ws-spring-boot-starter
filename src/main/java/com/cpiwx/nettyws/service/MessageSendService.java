package com.cpiwx.nettyws.service;

import com.cpiwx.nettyws.anaotations.Log;
import com.cpiwx.nettyws.entity.LogEntity;

/**
 * @author chenPan
 * @date 2023-08-30 09:38
 **/
public interface MessageSendService {


    void handleTimeoutAlertPush(Log aopLog, LogEntity logEntity);

    void handleErrorAlertPush(Log aopLog, LogEntity logEntity);

}
