package com.cpiwx.nettyws.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cpiwx.nettyws.anaotations.Log;
import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.entity.LogEntity;
import com.cpiwx.nettyws.model.dto.FeiShuDTO;
import com.cpiwx.nettyws.properties.PushProperties;
import com.cpiwx.nettyws.service.MessageSendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author chenPan
 * @date 2023-08-30 09:43
 **/
@Slf4j
public class FeiShuMessageSendServiceImpl implements MessageSendService {
    @Resource
    private PushProperties pushProperties;

    @Override
    public void handleTimeoutAlertPush(Log aopLog, LogEntity logEntity) {
        sendMessage("接口耗时过长告警", aopLog, logEntity);
    }

    @Override
    public void handleErrorAlertPush(Log aopLog, LogEntity logEntity) {
        sendMessage("接口异常告警", aopLog, logEntity);
    }

    private void sendMessage(String title, Log aopLog, LogEntity logEntity) {
        FeiShuDTO properties = pushProperties.getFeiShu();
        if (null == properties || !properties.isEnable()) {
            log.debug("飞书消息推送未启用");
            return;
        }
        JSONObject data = getFeiShuTemplate(title, aopLog, logEntity);
        if (StrUtil.isNotBlank(properties.getSecret())) {
            int timestamp = (int) (System.currentTimeMillis() / 1000);
            String signature = getSignature(timestamp, properties.getSecret());
            data.set("timestamp", timestamp);
            data.set("sign", signature);
        }
        sendRequest(properties.getHookUrl(), JSONUtil.toJsonStr(data));
    }

    public static String getSignature(int timestamp, String secret) {
        //把timestamp+"\n"+密钥当做签名字符串
        String stringToSign = timestamp + "\n" + secret;
        //使用HmacSHA256算法计算签名
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] signData = mac.doFinal(new byte[]{});
        return new String(Base64.encodeBase64(signData));
    }

    public JSONObject getFeiShuTemplate(String title, Log aopLog, LogEntity logEntity) {
        // https://open.feishu.cn/document/server-docs/im-v1/message-content-description/create_json
        List<List<JSONObject>> collect;
        if (Constants.LOG_TYPE_INFO.equals(logEntity.getLogType())) {
            collect = Arrays.asList(
                    addTextAsSingle("接口签名：" + logEntity.getMethod()),
                    addTextAsSingle("接口描述：" + aopLog.value()),
                    addTextAsSingle("接口耗时：" + logEntity.getTime() + " ms"),
                    addTextAsSingle("日志ID：" + logEntity.getLogId())
            );
        } else {
            collect = Arrays.asList(
                    addTextAsSingle("接口签名：" + logEntity.getMethod()),
                    addTextAsSingle("接口描述：" + aopLog.value()),
                    addTextAsSingle("接口耗时：" + logEntity.getTime() + " ms"),
                    addTextAsSingle("异常消息：" + logEntity.getExceptionDetail()),
                    addTextAsSingle("日志ID：" + logEntity.getLogId())
            );
        }
        JSONObject post = new JSONObject();
        JSONObject body = new JSONObject();
        JSONObject content = new JSONObject();
        body.set("title", title);

        body.set("content", collect);
        post.set("zh_cn", body);
        content.set("post", post);

        JSONObject obj = new JSONObject();
        obj.set("msg_type", "post");
        obj.set("content", content);
        return obj;
    }

    public JSONObject addText(String text) {
        JSONObject object = new JSONObject();
        object.set("tag", "text");
        object.set("text", text);
        return object;
    }

    public List<JSONObject> addTextAsSingle(String text) {
        return Collections.singletonList(addText(text));
    }

    public void sendRequest(String url, String data) {
        HttpResponse response = HttpUtil.createPost(url)
                .body(data)
                .timeout(5000)
                .header(Header.CONTENT_TYPE, "application/json")
                .execute();
        log.debug("飞书返回：code:{},message:{}", response.getStatus(), response.body());
        response.close();
    }
}
