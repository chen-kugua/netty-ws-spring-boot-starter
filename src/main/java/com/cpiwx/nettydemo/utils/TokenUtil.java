package com.cpiwx.nettydemo.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenPan
 * @date 2023-08-21 13:59
 **/
@Slf4j
public class TokenUtil {
    private static final ConcurrentHashMap<String, String> testUserContainer = new ConcurrentHashMap<>();

    public static boolean isValidToken(Object token) {
        if (null == token) {
            throw new IllegalArgumentException("未授权，请先登录");
        }
        String userId = testUserContainer.get(token.toString());
        return StrUtil.isNotBlank(userId);
    }

    public static void validateToken(String token) {
        String userId = testUserContainer.get(token);
        Assert.notBlank(userId, "无效token");
    }

    public static void putUser(String token, String userId) {
        testUserContainer.put(token, userId);
    }
}
