CREATE TABLE `sys_log`
(
    `log_id`           bigint(20) NOT NULL COMMENT 'ID',
    `description`      varchar(255) DEFAULT NULL COMMENT '接口描述',
    `log_type`         varchar(255) DEFAULT NULL COMMENT '日志类型INFO/ERROR',
    `operation_type`   varchar(255) DEFAULT NULL COMMENT '操作类型',
    `method`           varchar(255) DEFAULT NULL COMMENT '方法签名',
    `params`           text         DEFAULT NULL COMMENT '请求参数',
    `request_ip`       varchar(255) DEFAULT NULL COMMENT '请求ip',
    `time`             bigint(20)   DEFAULT NULL COMMENT '请求耗时ms',
    `user_id`          varchar(255) DEFAULT NULL COMMENT '用户ID',
    `address`          varchar(255) DEFAULT NULL COMMENT 'ip所在地址',
    `browser`          varchar(255) DEFAULT NULL COMMENT '浏览器',
    `exception_detail` text         DEFAULT NULL COMMENT '异常信息',
    `create_time`      datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`log_id`) USING BTREE,
    KEY `log_create_time_index` (`create_time`),
    KEY `inx_log_type` (`log_type`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3537
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT COMMENT ='系统日志';
