package nny.build.data.builder.service;

import nny.build.data.builder.config.DataSourceConfig;
import nny.build.data.builder.model.build.BuildSqlData;

import java.util.List;
import java.util.Map;

/**
 * Jdbc操作接口
 *
 * @author shengyong.huang
 * @date 2020-04-19
 */
public interface IJdbcService {

    /**
     * 执行Dml
     *
     * @param dataSourceConfig 数据库配置
     * @param sqlDataMap       sql数据Map集合
     * @return 执行结果
     */
    boolean executeDml(DataSourceConfig dataSourceConfig, Map<String, List<BuildSqlData>> sqlDataMap);

    /**
     * 执行ddl
     *
     * @param dataSourceConfig 数据库配置
     * @param sqlMap           sql数据Map集合
     * @return 执行结果
     */
    boolean executeDdl(DataSourceConfig dataSourceConfig, Map<String, List<String>> sqlMap);

    /**
     * 关闭数据库连接
     *
     * @param dataSourceConfig 数据库配置
     */
    void closeDbConnection(DataSourceConfig dataSourceConfig);
}
