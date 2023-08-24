package com.cpiwx.nettyws.service;

import io.netty.channel.ChannelPipeline;

/**
 * 实现该接口添加自定义处理器
 * @author chenPan
 * @date 2023-08-24 15:09
 **/
public interface CustomHandlerService {

    void addHandler(ChannelPipeline pipeline);
}
