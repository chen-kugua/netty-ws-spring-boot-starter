package com.cpiwx.nettyws.handler;

import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.properties.NettyProperties;
import com.cpiwx.nettyws.service.UserTokenService;
import com.cpiwx.nettyws.utils.ChannelAttrUtil;
import com.cpiwx.nettyws.utils.ParamUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @ChannelHandler.Sharable解决
 * Handler is not a @Sharable handler, so can't be added or removed multiple times
 * @author chenPan
 * @date 2023-08-24 14:27
 **/
@Slf4j
@ChannelHandler.Sharable
public class AuthWebSocketHandler extends ChannelInboundHandlerAdapter {
    @Setter(onMethod_ = @Autowired)
    private NettyProperties nettyProperties;
    @Setter(onMethod_ = @Autowired)
    private UserTokenService userTokenService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
            // id加入连接中 后续都可以取到
            ChannelAttrUtil.setAttr(ctx, Constants.IDENTITY_KEY, identity);
            // 校验token
            if (nettyProperties.isNeedCheckToken()) {
                String token = params.get(nettyProperties.getTokenKey());
                boolean validToken = userTokenService.checkToken(token);
                if (!validToken) {
                    log.warn("Invalid Token:【{}】,disConnect", token);
                    ctx.close();
                    return;
                }
                log.info("token验证通过：{}", token);
                ChannelAttrUtil.setAttr(ctx, Constants.TOKEN_KEY, token);
            }
            sendRedirect(request);
        }
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
    public void handlerAdded(ChannelHandlerContext ctx) {
        // ctx.channel().id() 表示唯一的值
        // channelRead之前调用 只有最开始建立连接时调用一次
        ChannelId id = ctx.channel().id();
        log.info("【{}】建立连接", id);
    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        String id = ctx.channel().id().asShortText();//表示唯一的值
        log.info("【{}】连接断开", id);
        String identity = ChannelAttrUtil.getIdentity(ctx);
        if (null != identity) {
            userTokenService.removeContext(identity);
        }
        ctx.close();
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常发生，异常消息 ", cause);
        String identity = ChannelAttrUtil.getIdentity(ctx);
        if (null != identity) {
            userTokenService.removeContext(identity);
        }
        ctx.close();
    }
}
