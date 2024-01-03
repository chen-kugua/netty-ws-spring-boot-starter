package com.cpiwx.nettyws.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author chenPan
 * @date 2023-08-25 11:23
 **/
@Slf4j
public class SqlUtils {
    private static final DataSource dataSource;

    static {
        dataSource = SpringContextHolder.getBean(DataSource.class);
    }

    private static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库连接异常", e);
        }
    }

    public static boolean checkTable(String tableName) {
        String dbName;
        Connection connection = getConnection();
        try {
            dbName = connection.getCatalog();
        } catch (SQLException e) {
            releaseConnection(connection);
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT TABLE_NAME\n")
                .append("        FROM INFORMATION_SCHEMA.TABLES\n")
                .append("        WHERE TABLE_SCHEMA = '")
                .append(dbName)
                .append("'\n")
                .append("        AND TABLE_NAME = '")
                .append(tableName)
                .append("';");
        String sql = sb.toString();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1) != null;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            releaseConnection(connection);
        }
    }

    public static void execute(String sql) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            releaseConnection(connection);
        }
    }

    private static void releaseConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("close connection error", e);
        }
    }

    public static String getInsertSqlFromObj(String tableName, Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringJoiner f = new StringJoiner(",", "(", ")");
        StringJoiner v = new StringJoiner(",", "(", ")");
        for (Field field : fields) {
            if ("serialVersionUID".equalsIgnoreCase(field.getName())) {
                continue;
            }
            field.setAccessible(true);
            Object fieldValue = ReflectUtil.getFieldValue(object, field);
            if (fieldValue != null) {
                f.add(StringUtils.toUnderScoreCase(field.getName()));
                if (fieldValue instanceof Date) {
                    v.add(StrUtil.format("'{}'", DateUtil.format((Date) fieldValue, DatePattern.NORM_DATETIME_FORMAT)));
                } else {
                    v.add(StrUtil.format("'{}'", fieldValue.toString()));
                }
            }
        }
        return StrUtil.format("insert into {} {} values {}", tableName, f, v);
    }

    public static String getUpdateSqlFromObj(String tableName, Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringJoiner v = new StringJoiner(",");
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = ReflectUtil.getFieldValue(object, field);
            if (fieldValue != null) {
                String fieldName = StringUtils.toUnderScoreCase(field.getName());
                if (fieldValue instanceof Date) {
                    v.add(fieldName + "=" + StrUtil.format("'{}'", DateUtil.format((Date) fieldValue, DatePattern.NORM_DATETIME_FORMAT)));
                } else {
                    v.add(fieldName + "=" + StrUtil.format("'{}'", fieldValue));
                }
            }
        }
        return StrUtil.format("UPDATE {} SET {}", tableName, v);
    }


}
