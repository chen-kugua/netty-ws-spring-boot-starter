package com.cpiwx.nettyws.enums;

import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author chenPan
 * @date 2023-08-25 10:12
 **/
@Getter
public enum OperationTypeEnum {
    /**
     * 查询
     */
    QUERY("查询"),
    /**
     * 新增
     */
    SAVE("新增"),
    /**
     * 修改
     */
    UPDATE("修改"),
    /**
     * 删除
     */
    DELETE("删除"),
    /**
     * 导入
     */
    IMPORT("导入"),
    /**
     * 导出
     */
    EXPORT("导出");

    private final String desc;

    OperationTypeEnum(String desc) {
        this.desc = desc;
    }
}
