package com.cpiwx.nettyws.handler;

import cn.hutool.json.JSONUtil;
import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.enums.ErrorCodeEnum;
import com.cpiwx.nettyws.enums.MessageTypeEnum;
import com.cpiwx.nettyws.model.Result;
import com.cpiwx.nettyws.model.dto.MessageDTO;
import com.cpiwx.nettyws.utils.RequestMappingUtil;
import com.cpiwx.nettyws.utils.WsMessageUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @Classname WsTextFrameHandler
 * @Description WsTextFrameHandler 自定义websocket处理器
 * @Date 2022/12/23 17:53
 * @Author chenPan
 */
@Slf4j
@ChannelHandler.Sharable
public class WsTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Setter(onMethod_ = @Autowired)
    private SingleChatHandler singleChatHandler;

    @Setter(onMethod_ = @Autowired)
    private GroupChatHandler groupChatHandler;

    @Setter(onMethod_ = @Autowired(required = false))
    private CustomMessageHandler customMessageHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        try {
            handleRequest(channelHandlerContext, textWebSocketFrame);
        } catch (Exception e) {
            log.error("处理ws消息异常", e);
            handleError(channelHandlerContext, e);
        }
    }

    /**
     * 处理消息
     *
     * @param ctx 通道上下文
     * @param msg 数据
     */
    // netty5.X版本为messageReceived方法 4.X为channelRead0
    // @Override
    protected void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        try {
            handleRequest(ctx, msg);
        } catch (Exception e) {
            log.error("处理ws消息异常", e);
            handleError(ctx, e);
        }
    }

    private void handleError(ChannelHandlerContext ctx, Exception e) {
        WsMessageUtil.sendMsg(ctx, Result.fail(Optional.ofNullable(e.getMessage()).orElse("系统空指针异常")));
    }

    private void handleRequest(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String body = msg.text();
        log.debug("服务器端收到消息 = " + body);
        if (Constants.PING.equals(body)) {
            log.debug("received ping");
            WsMessageUtil.sendMsg(ctx, Constants.PONG);
            return;
        }
        if (!JSONUtil.isTypeJSON(body)) {
            WsMessageUtil.sendMsg(ctx, Result.fail(ErrorCodeEnum.BODY_ERR));
            return;
        }
        MessageDTO messageDto = JSONUtil.toBean(body, MessageDTO.class);
        // 处理消息
        handleMessage(ctx, messageDto);
    }

    private void handleMessage(ChannelHandlerContext ctx, MessageDTO messageDto) {
        String type = messageDto.getType();
        if (MessageTypeEnum.API.name().equalsIgnoreCase(type)) {
            handleApi(ctx, messageDto);
        } else if (MessageTypeEnum.SINGLE_CHAT.name().equalsIgnoreCase(type)) {
            singleChatHandler.handle(ctx, messageDto);
        } else if (MessageTypeEnum.GROUP_CHAT.name().equalsIgnoreCase(type)) {
            groupChatHandler.handle(ctx, messageDto);
        } else {
            if (customMessageHandler != null) {
                customMessageHandler.handle(ctx, messageDto);
            } else {
                WsMessageUtil.sendMsg(ctx, Result.fail(ErrorCodeEnum.MESSAGE_TYPE_ERR));
            }
        }
    }

    private void handleApi(ChannelHandlerContext ctx, MessageDTO messageDto) {
        String content = messageDto.getContent();
        String apiPath = messageDto.getApiPath();
        try {
            Object res = RequestMappingUtil.handle(apiPath, content);
            WsMessageUtil.sendMsg(ctx, res);
        } catch (Exception e) {
            log.error("处理API请求时异常", e);
            String errMsg = Optional.ofNullable(e.getMessage()).orElse("系统空指针异常");
            WsMessageUtil.sendMsg(ctx, Result.fail(errMsg));
        }
    }

}
