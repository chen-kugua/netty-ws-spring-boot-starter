package com.cpiwx.nettyws.handler;

import com.cpiwx.nettyws.model.dto.OfflineMessageDTO;

import java.util.List;

/**
 * 离线消息处理
 * 当客户端断开连接时，服务端收到发给该客户端的消息，做离线消息存储
 * 客户端上线时，从离线消息队列中读取离线消息，发送给客户端
 *
 * @author chenPan
 * @date 2023-12-08 10:44
 **/
public abstract class OfflineMessageHandler {

    /**
     * 离线消息持久化
     *
     * @param dto 离线消息
     */
    public abstract void putMessage(OfflineMessageDTO dto);

    /**
     * 客户端上线时调用读取离线消息
     *
     * @param clientId 客户端ID（userId）
     * @return 消息体
     */
    public abstract List<OfflineMessageDTO> getMessage(String clientId);
}
