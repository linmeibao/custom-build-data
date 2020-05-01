package nny.build.data.builder.config;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.exception.DataSourceException;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 数据源信息
 *
 * @author shengyong.huang
 * @date 2020-02-25
 */
@Slf4j
@Getter
@Setter
public class DataSourceInfo {
    /**
     * 数据源Key
     */
    private String dbKey;
    /**
     * 驱动
     */
    private String driverClassName;
    /**
     * 数据库连接
     */
    private String jdbcUrl;
    /**
     * 用户
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    public Connection initializeConnection() {
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new DataSourceException(String.format("填充数据库连接失败, 异常信息{%s}", e.getMessage()));
        }
    }
}
