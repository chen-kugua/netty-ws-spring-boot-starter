package com.cpiwx.nettyws.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.entity.Message;
import com.cpiwx.nettyws.model.dto.MessageDTO;
import com.cpiwx.nettyws.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chen
 * @description 针对表【t_message】的数据库操作Service实现
 * @createDate 2023-12-19 10:34:48
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    @Resource
    private DataSource dataSource;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Async("offlineMessageExecutor")
    public void saveMessage(MessageDTO dto) {
        long msgId = IdUtil.getSnowflake(Constants.WORKER_ID, Constants.CENTER_ID).nextId();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "insert into t_message(id,from_id,to_id,msg_type,content_type,crt_time) values(?,?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, msgId);
            ps.setString(2, dto.getFromId());
            ps.setString(3, dto.getToId());
            ps.setString(4, dto.getType());
            ps.setString(5, dto.getContentType().name());
            ps.setDate(6, new Date(System.currentTimeMillis()));
            ps.execute();
            // 更新两人会话的最新一条消息
            updateSession(dto.getFromId(), dto.getToId(), msgId);
        } catch (SQLException e) {
            log.error("保存消息失败", e);
        }
    }

    @Override
    public void updateSession(String userID, String friendId, long msgId) {
        redisTemplate.opsForZSet().add(Constants.SESSION_LIST_PREFIX + userID, friendId, msgId);
        redisTemplate.opsForZSet().add(Constants.SESSION_LIST_PREFIX + friendId, userID, msgId);
    }


    @Override
    public void updateUnread(String userID, String friendId) {
        redisTemplate.opsForZSet().incrementScore(Constants.UNREAD_PREDIX + friendId, userID, 1);
    }

    @Override
    public Map<String,Integer> findUnreadCount(String userID) {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().rangeWithScores(Constants.UNREAD_PREDIX + userID, 0, -1);
        if (CollUtil.isEmpty(tuples)) {
            return Collections.emptyMap();
        }
        return tuples.stream().collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue, t -> t.getScore() != null ? t.getScore().intValue() : 0));
    }

    @Override
    public List<Message> findUnreadMessageByUserID(String userID, String friendUserId, int limit, int offset) {
        String sql = "select * from t_message where ((from_id=? and to_id=?) or(from_id=? and to_id=?) ) order by crt_time desc limit ?,?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setString(2, friendUserId);
            ps.setString(3, friendUserId);
            ps.setString(4, userID);
            ps.setInt(5, offset);
            ps.setInt(6, limit);
            ResultSet rs = ps.executeQuery();
            List<Message> res = new ArrayList<>();
            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getLong("id"));
                message.setFromId(rs.getString("from_id"));
                message.setToId(rs.getString("to_id"));
                message.setContent(rs.getString("content"));
                message.setCrtTime(rs.getTimestamp("crt_time"));
                message.setMsgType(rs.getString("msg_type"));
                message.setContentType(rs.getString("content_type"));
                message.setReadFlag(rs.getInt("read_flag"));
                res.add(message);
            }
            res.sort(Comparator.comparing(Message::getCrtTime));
            return res;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

}




