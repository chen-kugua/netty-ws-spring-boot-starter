package com.cpiwx.nettyws.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cpiwx.nettyws.anaotations.Log;
import com.cpiwx.nettyws.constant.Constants;
import com.cpiwx.nettyws.entity.LogEntity;
import com.cpiwx.nettyws.service.LogService;
import com.cpiwx.nettyws.service.MessageSendService;
import com.cpiwx.nettyws.utils.FileUtils;
import com.cpiwx.nettyws.utils.SqlUtils;
import com.cpiwx.nettyws.utils.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jinjin
 * @date 2020-09-27
 */
@Slf4j
@Service
public class LogServiceImpl implements LogService {
    @Setter(onMethod_ = @Autowired(required = false))
    private Map<String, MessageSendService> messageSender;

    private final TimedCache<String, String> cache = CacheUtil.newTimedCache(60);

    {
        // 定时清理失效的 30s一次 get的时候过期也会清理
        cache.schedulePrune(30000);
    }

    @Override
    public void save(String userId, String browser, String ip, ProceedingJoinPoint joinPoint, LogEntity logEntity) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log aopLog = method.getAnnotation(Log.class);
        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";

        // 描述
        logEntity.setDescription(aopLog.value());
        logEntity.setRequestIp(ip);
        // 操作类型
        logEntity.setOperationType(aopLog.operationType().name());

        logEntity.setAddress(StringUtils.getCityInfo(logEntity.getRequestIp()));
        logEntity.setMethod(methodName);
        logEntity.setUserId(userId);
        logEntity.setParams(getParameter(method, joinPoint.getArgs()));
        logEntity.setBrowser(browser);
        if (logEntity.getLogId() == null) {
            logEntity.setLogId(IdUtil.getSnowflake(Constants.WORKER_ID,Constants.CENTER_ID).nextId());
            insert(logEntity);
        } else {
            update(logEntity);
        }
        if (aopLog.timeoutEarlyWarning() || aopLog.errorEarlyWarning()) {
            handleWarning(aopLog, logEntity);
        }
    }

    private boolean setIfAbsent(String key, String value, int timeout) {
        String intern = key.intern();
        synchronized (intern) {
            String v = cache.get(key, false);
            if (null == v) {
                cache.put(key, value, timeout);
                return true;
            }
        }
        return false;
    }

    private void handleWarning(Log aopLog, LogEntity logEntity) {
        if (aopLog.timeoutEarlyWarning()) {
            // 超时 ms
            int threshold = aopLog.timeThreshold();
            Long speedTime = logEntity.getTime();
            if (speedTime > threshold) {
                boolean success = this.setIfAbsent(Constants.TIMEOUT_WARNING_PREFIX + logEntity.getMethod(), Constants.YES, aopLog.interval());
                if (success) {
                    log.warn("超时告警：{}，{}", logEntity.getMethod(), speedTime);
                    if (CollUtil.isEmpty(messageSender)) {
                        return;
                    }
                    Collection<MessageSendService> handlers = messageSender.values();
                    for (MessageSendService handler : handlers) {
                        handler.handleTimeoutAlertPush(aopLog, logEntity);
                    }
                }
            }
        }
        if (aopLog.errorEarlyWarning()) {
            boolean success = this.setIfAbsent(Constants.ERROR_WARNING_PREFIX + logEntity.getMethod(), Constants.YES, aopLog.interval());
            if (success) {
                log.warn("异常告警：{}，{}", logEntity.getMethod(), logEntity.getExceptionDetail());
                if (CollUtil.isEmpty(messageSender)) {
                    return;
                }
                Collection<MessageSendService> handlers = messageSender.values();
                for (MessageSendService handler : handlers) {
                    handler.handleErrorAlertPush(aopLog, logEntity);
                }
            }
        }
    }

    @Override
    public void initTable() {
        boolean tableExists = SqlUtils.checkTable(Constants.LOG_TABLE_NAME);
        if (!tableExists) {
            log.info("系统日志表不存在。。新建");
            String sql = FileUtils.classPathFile2Text(Constants.INIT_SQL_PATH);
            log.info("建表：{}", sql);
            SqlUtils.execute(sql);
        }
    }

    private void update(LogEntity logEntity) {
        String sql = SqlUtils.getUpdateSqlFromObj(Constants.LOG_TABLE_NAME, logEntity);
        SqlUtils.execute(sql);
    }

    private void insert(LogEntity logEntity) {
        String sql = SqlUtils.getInsertSqlFromObj(Constants.LOG_TABLE_NAME, logEntity);
        SqlUtils.execute(sql);
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private String getParameter(Method method, Object[] args) {
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            //将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argList.add(args[i]);
            }
            //将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                Map<String, Object> map = new HashMap<>();
                String key = parameters[i].getName();
                if (!StrUtil.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                map.put(key, args[i]);
                argList.add(map);
            }
        }
        if (argList.isEmpty()) {
            return "";
        }
        return argList.size() == 1 ? JSONUtil.toJsonStr(argList.get(0)) : JSONUtil.toJsonStr(argList);
    }


}
