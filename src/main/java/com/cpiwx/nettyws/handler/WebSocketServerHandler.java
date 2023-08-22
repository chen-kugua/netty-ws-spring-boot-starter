package com.cpiwx.nettyws.handler;

import cn.hutool.json.JSONUtil;
import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.enums.ErrorCodeEnum;
import com.cpiwx.nettyws.enums.MessageTypeEnum;
import com.cpiwx.nettyws.model.Result;
import com.cpiwx.nettyws.model.dto.MessageDTO;
import com.cpiwx.nettyws.properties.NettyProperties;
import com.cpiwx.nettyws.service.CheckTokenService;
import com.cpiwx.nettyws.service.MessageHandler;
import com.cpiwx.nettyws.utils.RequestMappingUtil;
import com.cpiwx.nettyws.utils.WsMessageUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Setter(onMethod_ = @Autowired)
    private NettyProperties nettyProperties;

    @Setter(onMethod_ = @Autowired(required = false))
    private CheckTokenService checkTokenService;

    @Setter(onMethod_ = @Autowired(required = false))
    private MessageHandler messageHandler;

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

    /**
     * 首次建立连接和 每次收到消息都会进入
     *
     * @param ctx 通道
     * @param msg 数据
     * @throws Exception 异常
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 第一次建立连接时校验Token
        if (msg instanceof FullHttpRequest) {
            if (nettyProperties.isNeedCheckToken()) {
                FullHttpRequest request = (FullHttpRequest) msg;
                HttpHeaders headers = request.headers();
                CharSequence token = headers.get(Constants.HEADER_TOKEN);
                if (null == checkTokenService) {
                    log.error("checkToken为true但是未实现checkTokenService");
                    ctx.channel().disconnect();
                    return;
                }
                boolean validToken = checkTokenService.checkToken(token);
                if (!validToken) {
                    log.warn("Invalid Token:【{}】,disConnect", token);
                    ctx.disconnect();
                    WsMessageUtil.removeUser(ctx);
                    return;
                }
                log.info("token验证通过：{}", token);
            }
        }
        // 处理消息
        super.channelRead(ctx, msg);
    }

    /**
     * 处理消息
     *
     * @param ctx 通道上下文
     * @param msg 数据
     */
    @Override
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
        log.info("服务器端收到消息 = " + body);
        if (Constants.PING.equals(body)) {
            log.debug("received ping");
            WsMessageUtil.sendMsg(ctx, Constants.PONG);
            return;
        }
        if (!JSONUtil.isJson(body)) {
            WsMessageUtil.sendMsg(ctx, Result.fail(ErrorCodeEnum.body_err));
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
        } else {
            if (messageHandler != null) {
                messageHandler.handle(ctx, messageDto);
            }
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
        String id = ctx.channel().id().asShortText();//表示唯一的值
        log.info("【{}】连接断开", id);
        WsMessageUtil.removeUser(ctx);
    }


    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常发生，异常消息 ", cause);
        WsMessageUtil.removeUser(ctx);
    }


}
