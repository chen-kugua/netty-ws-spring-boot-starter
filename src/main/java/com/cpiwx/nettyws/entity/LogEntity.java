package com.cpiwx.nettyws.entity;

import com.cpiwx.nettyws.enums.OperationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jinjin
 * @date 2020-09-27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class LogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long logId;

    private String description;

    /**
     * INFO/ERROR
     */
    private String logType;

    /**
     * {@link OperationTypeEnum#name()}
     */
    private String operationType;

    private String method;

    private String params;

    private String requestIp;

    private Long time;

    private String userId;

    private String address;

    private String browser;

    private String exceptionDetail;

    private Date createTime;

    public LogEntity(String logType, Long time) {
        this.logType = logType;
        this.time = time;
    }
}
