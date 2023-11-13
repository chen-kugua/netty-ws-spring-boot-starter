# 基于netty实现的websocket服务器、实现了类似SpringMVC的接口请求功能和单聊、群聊、自定义消息

# 开始使用
1、pom中添加
```xml
        <dependency>
            <groupId>com.cpiwx</groupId>
            <artifactId>netty-ws-spring-boot-starter</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
```
2、启动类上添加@EnableWS  

3、修改配置文件（可选)
```properties
# 处理客户端连接的线程组数量，如果只有一个端口则设置不生效 默认为1
netty.ws.boss-num=2
# 真正处理请求的线程组数量 设为0则根据CPU核心数*2
netty.ws.worker-num=0
#服务端口
netty.ws.port=9000,9001
#第一次连接时是否需要校验token
netty.ws.need-check-token=false
# 它表示在尝试建立连接时，等待连接成功的最大时间，超过这个时间仍未建立连接则会视为连接超时。
netty.ws.timeout=30000
# 需要校验token时指定URL中token的字段名
netty.ws.token-key=token
# 服务端点
netty.ws.endpoint=/ws
# 需要使用单聊和群聊时指定用户唯一id的名称
netty.ws.identity-key=userId
#ws帧最大值 传大数据时可以调大
netty.ws.max-frame-size=65536
```
4、像MVC一样编写基于ws的接口 
> 将@Controller、@RestController替换为[WsController.java](src%2Fmain%2Fjava%2Fcom%2Fcpiwx%2Fnettyws%2Fanaotations%2FWsController.java)  
> 将@GetMapping、@PostMapping替换为 [Request.java](src%2Fmain%2Fjava%2Fcom%2Fcpiwx%2Fnettyws%2Fanaotations%2FRequest.java)  

示例
```java
package com.cpiwx.nettyws.controller;

import com.cpiwx.nettyws.anaotations.Param;
import com.cpiwx.nettyws.anaotations.Request;
import com.cpiwx.nettyws.anaotations.WsController;
import com.cpiwx.nettyws.model.Result;
import com.cpiwx.nettyws.model.dto.LoginDTO;
import com.cpiwx.nettyws.service.TestMessageService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author chenPan
 * @date 2023-08-21 16:21
 **/
@WsController
@Slf4j
public class MessageController {
    @Resource
    private TestMessageService messageService;

    @Request("/test/id")
    public Result<String> testId() {
        return Result.ok(messageService.test());
    }

    @Request("/test/login")
    public Result<String> testLogin(LoginDTO dto) {
        return Result.ok(messageService.testLogIn(dto));
    }

    @Request("/test/params")
    public Result<String> testParams(String userId, @Param("loginDto") LoginDTO dto,Integer integer) {
        log.info("userId:{}，dto:{}，int:{}", userId, dto,integer);
        return Result.ok("ok");
    }

}

```


# 主要接口
1、com.cpiwx.nettyws.service.CustomHandlerService  
实现该接口重写addHandler()方法即可添加自定义处理器  

2、com.cpiwx.nettyws.handler.UserTokenHandler  
实现该接口可自定义token校验和客户端映射关系维护  

3、com.cpiwx.nettyws.handler.CustomMessageHandler  
实现该接口可以处理自定义消息类型
默认支持
```java
com.cpiwx.nettyws.enums.MessageTypeEnum
    API(1, "访问api"),
    SINGLE_CHAT(2, "单聊"),
    GROUP_CHAT(3, "群聊");
```
4、com.cpiwx.nettyws.handler.SingleChatHandler   
实现该接口 自定义单聊逻辑

5、com.cpiwx.nettyws.handler.GroupChatHandler  
实现该接口 自定义群聊逻辑

# 主要注解
1、@WsController  
类似@Controller+@RequestMapping value指定基础路径

2、@Request  
类似@GetMapping、@PostMapping 指定urlPath

3、@Body  
类似@RequestBody 将请求体数据转换为标注的实体

4、@Param  
类似@RequestParam 指定key名 从请求数据中取值

5、@EnableWs  
启动ws服务

