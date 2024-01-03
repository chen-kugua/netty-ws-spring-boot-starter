package com.cpiwx.nettyws.config;

import com.cpiwx.nettyws.aspect.LogAspect;
import com.cpiwx.nettyws.config.thread.AsyncTaskExecutePool;
import com.cpiwx.nettyws.properties.AsyncTaskProperties;
import com.cpiwx.nettyws.properties.PushProperties;
import com.cpiwx.nettyws.service.MessageSendService;
import com.cpiwx.nettyws.service.impl.FeiShuMessageSendServiceImpl;
import com.cpiwx.nettyws.service.impl.LogServiceImpl;
import com.cpiwx.nettyws.service.impl.MailMessageSendServiceImpl;
import com.cpiwx.nettyws.utils.SpringContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author chenPan
 * @date 2023-08-25 14:27
 **/
@EnableAsync
@EnableConfigurationProperties({AsyncTaskProperties.class, PushProperties.class})
@Import({LogAspect.class, AsyncTaskExecutePool.class,InitTask.class, LogServiceImpl.class, SpringContextHolder.class})
@Configuration
public class AutoConfig {

    @Bean("feiShuMessageSendService")
    @ConditionalOnProperty(prefix = "notice.push.fei-shu",name = "enable", havingValue = "true")
    public MessageSendService feiShuMessageSendService() {
        return new FeiShuMessageSendServiceImpl();
    }


    @Bean("mailMessageSendService")
    @ConditionalOnProperty(prefix = "notice.push.mail",name = "enable", havingValue = "true")
    public MessageSendService mailMessageSendService() {
        return new MailMessageSendServiceImpl();
    }
}
