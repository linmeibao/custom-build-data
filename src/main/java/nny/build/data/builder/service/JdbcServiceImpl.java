package nny.build.data.builder.service;

import nny.build.data.builder.config.DataSourceConfig;
import nny.build.data.builder.exception.BuilderException;
import nny.build.data.builder.model.build.BuildSqlData;
import nny.build.data.builder.model.table.TableColumn;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Jdbc操作实现类
 *
 * @author shengyong.huang
 * @date 2020-04-19
 */
@Slf4j
public class JdbcServiceImpl implements IJdbcService {

    /**
     * 执行DML
     *
     * @param dataSourceConfig 数据库配置
     * @param sqlDataMap       sql数据Map
     * @return 执行结果
     */
    @Override
    public boolean executeDml(DataSourceConfig dataSourceConfig, Map<String, List<BuildSqlData>> sqlDataMap) {

        String curSqlStatement = null;
        try {
            for (Map.Entry<String, List<BuildSqlData>> entry : sqlDataMap.entrySet()) {

                Connection connection = dataSourceConfig.getConnectionMap().get(entry.getKey());

                List<BuildSqlData> buildSqlDataList = entry.getValue();

                for (BuildSqlData buildSqlData : buildSqlDataList) {

                    PreparedStatement preparedStatement = connection.prepareStatement(buildSqlData.getSqlStatement());

                    for (List<TableColumn> tableColumns : buildSqlData.getColumnDataList()) {

                        for (int i = 0; i < tableColumns.size(); i++) {

                            TableColumn tableColumn = tableColumns.get(i);

                            Object value = tableColumn.getColumnValue();

                            switch (tableColumn.getDataType()) {
                                case STRING:
                                case DATE:
                                case TIMESTAMP:
                                    preparedStatement.setString(i + 1, value == null ? null : value.toString());
                                    break;
                                default:
                                    preparedStatement.setObject(i + 1, tableColumn.getColumnValue());
                                    break;
                            }
                        }
                        preparedStatement.addBatch();
                    }

                    curSqlStatement = buildSqlData.getSqlStatement();

                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                    preparedStatement.close();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new BuilderException(String.format("执行DML失败 当前sqlStatement:{%s}", curSqlStatement));
        }
        return true;
    }

    /**
     * 执行DDL
     *
     * @param dataSourceConfig 数据库配置
     * @param sqlMap           sql数据集合
     * @return 执行结果
     */
    @Override
    public boolean executeDdl(DataSourceConfig dataSourceConfig, Map<String, List<String>> sqlMap) {

        for (String dbKey : sqlMap.keySet()) {

            Connection connection = dataSourceConfig.getConnectionMap().get(dbKey);

            try {
                Statement statement = connection.createStatement();
                for (String sql : sqlMap.get(dbKey)) {
                    statement.addBatch(sql);
                }

                statement.executeBatch();
                statement.clearBatch();

                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw new BuilderException("执行DDL失败");
            }
        }
        return true;
    }

    /**
     * 关闭连接
     *
     * @param dataSourceConfig 数据库配置
     */
    @Override
    public void closeDbConnection(DataSourceConfig dataSourceConfig) {
        // 关闭连接
        for (Map.Entry<String, Connection> entry : dataSourceConfig.getConnectionMap().entrySet()) {
            try {
                entry.getValue().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        log.info("数据库连接已释放");
    }
}
