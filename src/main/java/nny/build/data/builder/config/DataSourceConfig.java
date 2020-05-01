package nny.build.data.builder.config;

import nny.build.data.builder.exception.DataSourceException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源配置
 *
 * @author shengyong.huang
 * @date 2020-02-23
 */
@Slf4j
@Getter
@Setter
public class DataSourceConfig {

    /**
     * 数据源列表
     */
    private List<DataSourceInfo> dataSourceInfos;

    /**
     * 自动执行DDL
     */
    private Boolean autoDDl;

    /**
     * 数据库连接
     */
    private Map<String, Connection> connectionMap = new HashMap<>();

    /**
     * 填充数据库连接
     */
    public void fillConnections() {
        if (this.dataSourceInfos == null) {
            throw new DataSourceException("填充数据库连接失败, dataSourceInfos 不能为空");
        }

        for (DataSourceInfo source : dataSourceInfos) {
            connectionMap.put(source.getDbKey(), source.initializeConnection());
        }

        log.info("填充数据库连接完成 , {}", dataSourceInfos);
    }
}
