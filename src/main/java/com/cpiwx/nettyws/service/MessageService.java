package com.cpiwx.nettyws.service;


import com.cpiwx.nettyws.domain.Message;
import com.cpiwx.nettyws.model.dto.MessageDTO;

import java.util.List;
import java.util.Map;

/**
 * @author chen
 * @description 针对表【t_message】的数据库操作Service
 * @createDate 2023-12-19 10:34:48
 */
public interface MessageService {

    /**
     * 保存消息
     *
     * @param dto 消息内容
     */
    void saveMessage(MessageDTO dto);

    /**
     * 更新会话列表 最新一条消息
     * saveMessage 完成后有调用
     *
     * @param userID   发送方ID
     * @param friendId 接收方ID
     * @param msgId    消息ID
     */
    void updateSession(String userID, String friendId, long msgId);

    /**
     * 更新未读数
     *
     * @param userID   发送方ID
     * @param friendId 接收方ID
     */
    void updateUnread(String userID, String friendId);

    /**
     * 获取会话未读数
     *
     * @param userID 用户ID
     * @return Map<friendId, count>
     */
    Map<String, Integer> findUnreadCount(String userID);


    List<Message> findUnreadMessageByUserID(String userID, String friendUserId, int limit, int offset);

}
