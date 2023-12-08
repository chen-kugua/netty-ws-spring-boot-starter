package com.cpiwx.nettyws.handler;

import com.cpiwx.nettyws.model.dto.OfflineMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chenPan
 * @date 2023-12-08 13:42
 **/
@Slf4j
public class OfflineMessageHandlerDefaultImpl extends OfflineMessageHandler {
    ConcurrentHashMap<String, CopyOnWriteArrayList<OfflineMessageDTO>> offlineMessageMap = new ConcurrentHashMap<>();

    /**
     * 离线消息持久化
     *
     * @param dto 离线消息
     */
    @Override
    @Async("offlineMessageExecutor")
    public void putMessage(OfflineMessageDTO dto) {
        String toId = dto.getToId();
        String intern = toId.intern();
        synchronized (intern) {
            CopyOnWriteArrayList<OfflineMessageDTO> list = offlineMessageMap.get(toId);
            if (list == null) {
                list = new CopyOnWriteArrayList<>();
            }
            list.add(dto);
            offlineMessageMap.put(dto.getToId(), list);
        }
    }

    /**
     * 客户端上线时调用读取离线消息
     *
     * @param clientId 客户端ID（userId）
     * @return 消息体
     */
    @Override
    public List<OfflineMessageDTO> getMessage(String clientId) {
        return offlineMessageMap.get(clientId);
    }
}
