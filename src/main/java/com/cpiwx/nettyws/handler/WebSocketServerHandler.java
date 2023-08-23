package com.cpiwx.nettyws.handler;

import cn.hutool.json.JSONUtil;
import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.enums.ErrorCodeEnum;
import com.cpiwx.nettyws.enums.MessageTypeEnum;
import com.cpiwx.nettyws.model.Result;
import com.cpiwx.nettyws.model.dto.MessageDTO;
import com.cpiwx.nettyws.properties.NettyProperties;
import com.cpiwx.nettyws.service.UserTokenService;
import com.cpiwx.nettyws.utils.ChannelAttrUtil;
import com.cpiwx.nettyws.utils.ParamUtil;
import com.cpiwx.nettyws.utils.RequestMappingUtil;
import com.cpiwx.nettyws.utils.WsMessageUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

/**
 * @Classname WebSocketServerHandler
 * @Description WebSocketServerHandler 自定义websocket处理器
 * @Date 2022/12/23 17:53
 * @Author chenPan
 */
@Slf4j
@ChannelHandler.Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Setter(onMethod_ = @Autowired)
    private NettyProperties nettyProperties;

    @Setter(onMethod_ = @Autowired(required = false))
    private SingleChatHandler singleChatHandler;

    @Setter(onMethod_ = @Autowired(required = false))
    private GroupChatHandler groupChatHandler;

    @Setter(onMethod_ = @Autowired(required = false))
    private CustomMessageHandler customMessageHandler;

    @Setter(onMethod_ = @Autowired)
    private UserTokenService userTokenService;

    /**
     * 首次建立连接和 每次收到消息都会进入
     *
     * @param ctx 通道
     * @param msg 数据
     * @throws Exception 异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 第一次建立连接时校验Token重置URL 这时连接未正式建立 不能给客户端发消息
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            // 从URL获取参数
            Map<String, String> params = ParamUtil.getUriParams(uri);
            String identity = params.get(nettyProperties.getIdentityKey());
            if (null == identity) {
                identity = ctx.channel().id().asShortText();
            }
            // 维护客户端映射关系
            userTokenService.putContext(identity, ctx);
            ChannelAttrUtil.setAttr(ctx, Constants.IDENTITY_KEY, identity);
            // 校验token
            if (nettyProperties.isNeedCheckToken()) {
                String token = params.get(nettyProperties.getTokenKey());
                boolean validToken = userTokenService.checkToken(token);
                if (!validToken) {
                    log.warn("Invalid Token:【{}】,disConnect", token);
                    ctx.disconnect();
                    return;
                }
                log.info("token验证通过：{}", token);
                ChannelAttrUtil.setAttr(ctx, Constants.TOKEN_KEY, token);
            }
            sendRedirect(request);
        }
        // 处理消息
        super.channelRead(ctx, msg);
    }

    private void sendRedirect(FullHttpRequest request) {
        String uri = request.uri();
        if (uri.contains("?")) {
            String newUri = uri.substring(0, uri.indexOf("?"));
            log.debug("重定向uri：{}", newUri);
            request.setUri(newUri);
        }
    }

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
        log.info("服务器端收到消息 = " + body);
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
        String identity = ChannelAttrUtil.getAttr(ctx, Constants.IDENTITY_KEY, String.class);
        if (null != identity) {
            userTokenService.removeContext(identity);
        }
    }


}
