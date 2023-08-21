package com.cpiwx.nettydemo.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.cpiwx.nettydemo.constant.Constants;
import com.cpiwx.nettydemo.enums.ErrorCodeEnum;
import com.cpiwx.nettydemo.enums.MessageTypeEnum;
import com.cpiwx.nettydemo.model.Result;
import com.cpiwx.nettydemo.model.dto.LoginDTO;
import com.cpiwx.nettydemo.model.dto.MessageDTO;
import com.cpiwx.nettydemo.utils.RequestMappingUtil;
import com.cpiwx.nettydemo.utils.TokenUtil;
import com.cpiwx.nettydemo.utils.WsMessageUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @Classname WebSocketServerHandler
 * @Description WebSocketServerHandler 自定义websocket处理器
 * @Date 2022/12/23 17:53
 * @Author chenPan
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 经过测试，在 ws 的 uri 后面不能传递参数，不然在 netty 实现 websocket 协议握手的时候会出现断开连接的情况。
     * 针对这种情况在 websocketHandler 之前做了一层 地址过滤，然后重写
     * request 的 uri，并传入下一个管道中，基本上解决了这个问题。
     * <p>
     * 连接时不带参数可以在发送消息时带上消息type类型  如果类型为建立连接 则把参数中带的id加入到用户池中管理  如果为其他类型消息
     * 则先校验 是否已被池子管理 否则抛异常断开连接
     */
    // @Override
    // public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //     if (msg instanceof FullHttpRequest) {
    //         FullHttpRequest request = (FullHttpRequest) msg;
    //         String uri = request.uri();
    //         String token = request.headers().get("token");
    //         String origin = request.headers().get("Origin");
    //         log.info("token:{}", token);
    //         log.info("origin:{}", origin);
    //
    //         if (null != uri && uri.contains(Constants.DEFAULT_WEB_SOCKET_LINK) && uri.contains("?")) {
    //             String[] uriArray = uri.split("\\?");
    //             Map<String, String> params = new HashMap<>();
    //             for (String param : uriArray) {
    //                 String[] split = param.split("=");
    //                 if (split.length == 2) {
    //                     params.put(split[0], split[1]);
    //                 }
    //             }
    //             String userId = params.get(Constants.USER_ID);
    //             if (userId != null) {
    //                 log.info("用户{}上线", userId);
    //                 String sessionId = ctx.channel().id().asShortText();
    //                 userId2SessionIdMap.put(userId, sessionId);
    //                 onlineContainer.put(sessionId, ctx);
    //             }
    //             request.setUri(Constants.DEFAULT_WEB_SOCKET_LINK);
    //         }
    //     }
    //     super.channelRead(ctx, msg);
    // }

    // 读取客户端发送的请求报文
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        try {
            handleRequest(ctx, msg);
        } catch (Exception e) {
            log.error("处理ws消息异常", e);
            handleError(ctx, e);
        }
    }

    private void handleError(ChannelHandlerContext ctx, Exception e) {
        WsMessageUtil.sendMsg(ctx, Result.fail(Optional.ofNullable(e.getMessage()).orElse("系统NPE异常")));
    }

    private void handleRequest(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String body = msg.text();
        log.info("服务器端收到消息 = " + body);
        if (!JSONUtil.isJson(body)) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(Result.fail(ErrorCodeEnum.body_err))));
            return;
        }
        MessageDTO messageDto = JSONUtil.toBean(body, MessageDTO.class);
        String type = messageDto.getType();
        if (MessageTypeEnum.CONNECT.name().equals(type)) {
            log.info("connect。。。。。。");
            String content = messageDto.getContent();
            LoginDTO dto = JSONUtil.toBean(content, LoginDTO.class);
            String token = IdUtil.simpleUUID();
            WsMessageUtil.putUser(dto.getUserId(), ctx);
            TokenUtil.putUser(token, dto.getUserId());
            ctx.channel().attr(AttributeKey.valueOf(Constants.HEADER_TOKEN)).set(token);
            ctx.channel().attr(AttributeKey.valueOf(Constants.USER_ID)).set(dto.getUserId());
            WsMessageUtil.sendMsg(ctx, Result.ok("login success"));
            return;
        }
        Object token = ctx.channel().attr(AttributeKey.valueOf(Constants.HEADER_TOKEN)).get();
        boolean validToken = TokenUtil.isValidToken(token);
        if (!validToken) {
            ctx.disconnect();
            WsMessageUtil.removeUser(ctx);
            return;
        }
        log.info("token验证通过");
        // 处理消息
        handleMessage(ctx, messageDto);
    }

    private void handleMessage(ChannelHandlerContext ctx, MessageDTO messageDto) {
        MessageTypeEnum typeEnum = messageDto.getTypeEnum();
        switch (typeEnum) {
            case API:
                handleApi(ctx, messageDto);
                break;
        }

    }

    private void handleApi(ChannelHandlerContext ctx, MessageDTO messageDto) {
        String content = messageDto.getContent();
        String apiPath = messageDto.getApiPath();
        Object res = RequestMappingUtil.handle(apiPath, content);
        WsMessageUtil.sendMsg(ctx, res);
    }

    // 当web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // ctx.channel().id() 表示唯一的值
        ChannelId id = ctx.channel().id();
        log.info("【{}】建立连接", id);
    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        // ctx.channel().id() 表示唯一的值
        WsMessageUtil.removeUser(ctx);
    }


    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常发生，异常消息 ", cause);
        WsMessageUtil.removeUser(ctx);
    }


}
