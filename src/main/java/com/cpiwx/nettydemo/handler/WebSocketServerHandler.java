package com.cpiwx.nettydemo.handler;

import cn.hutool.core.date.DateUtil;
import com.cpiwx.nettydemo.constant.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Classname WebSocketServerHandler
 * @Description WebSocketServerHandler 自定义websocket处理器
 * @Date 2022/12/23 17:53
 * @Author chenPan
 */
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    ConcurrentHashMap<String, ChannelHandlerContext> onlineContainer = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, String> userId2SessionIdMap = new ConcurrentHashMap<>();

    /**
     * 经过测试，在 ws 的 uri 后面不能传递参数，不然在 netty 实现 websocket 协议握手的时候会出现断开连接的情况。
     * 针对这种情况在 websocketHandler 之前做了一层 地址过滤，然后重写
     * request 的 uri，并传入下一个管道中，基本上解决了这个问题。
     * <p>
     * 连接时不带参数可以在发送消息时带上消息type类型  如果类型为建立连接 则把参数中带的id加入到用户池中管理  如果为其他类型消息
     * 则先校验 是否已被池子管理 否则抛异常断开连接
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            String token = request.headers().get("token");
            String origin = request.headers().get("Origin");
            log.info("token:{}", token);
            log.info("origin:{}", origin);

            if (null != uri && uri.contains(Constants.DEFAULT_WEB_SOCKET_LINK) && uri.contains("?")) {
                String[] uriArray = uri.split("\\?");
                Map<String, String> params = new HashMap<>();
                for (String param : uriArray) {
                    String[] split = param.split("=");
                    if (split.length == 2) {
                        params.put(split[0], split[1]);
                    }
                }
                String userId = params.get(Constants.USER_ID);
                if (userId != null) {
                    log.info("用户{}上线", userId);
                    String sessionId = ctx.channel().id().asShortText();
                    userId2SessionIdMap.put(userId, sessionId);
                    onlineContainer.put(sessionId, ctx);
                }
                request.setUri(Constants.DEFAULT_WEB_SOCKET_LINK);
            }
        }
        super.channelRead(ctx, msg);
    }

    // 读取客户端发送的请求报文
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器端收到消息 = " + msg.text());
        // 回复消息
        ctx.channel().writeAndFlush(new TextWebSocketFrame(DateUtil.now() + "服务器回复:" + msg.text()));
    }

    // 当web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // ctx.channel().id() 表示唯一的值
        System.out.println("handlerAdded 被调用， channel.id.longText = " + ctx.channel().id().asLongText());
        System.out.println("handlerAdded 被调用， channel.id.shortText = " + ctx.channel().id().asShortText());
    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        // ctx.channel().id() 表示唯一的值
        String sessionId = ctx.channel().id().asShortText();
        System.out.println("sessionId = " + sessionId + "断开连接");
        onlineContainer.remove(sessionId);
        removeUser(sessionId);
    }

    private String getUserId(String sessionId) {
        for (Map.Entry<String, String> entry : userId2SessionIdMap.entrySet()) {
            if (sessionId.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void removeUser(String sessionId) {
        if (null != sessionId) {
            String userId = getUserId(sessionId);
            if (userId != null) {
                userId2SessionIdMap.remove(sessionId);
            }
        }
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生，异常消息 = " + cause.getMessage());
        String sessionId = ctx.channel().id().asShortText();
        // 关闭连接
        ctx.channel().close();
        onlineContainer.remove(sessionId);
        String userId = getUserId(sessionId);
        if (userId != null) {
            userId2SessionIdMap.remove(userId);
        }
    }


}
