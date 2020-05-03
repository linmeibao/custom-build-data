package nny.build.data.builder.config;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.enums.OperateMode;
import nny.build.data.builder.enums.SqlTypeEnum;
import nny.build.data.builder.exception.DataSourceException;
import nny.build.data.builder.model.BusinessDataJson;
import nny.build.data.builder.model.Scenario;
import nny.build.data.builder.model.rule.ValueRule;
import nny.build.data.builder.model.table.TableColumn;
import nny.build.data.builder.model.table.TableIndex;
import nny.build.data.builder.model.table.TableInfo;
import nny.build.data.builder.utils.CommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 序列化示例文件配置
 *
 * @author shengyong.huang
 * @date 2020-05-01
 */
@Slf4j
@Getter
@Setter
public class SerializeExampleConfig {

    /**
     * 示例文件输出文件路径
     */
    private String generateExampleFilePath;
    /**
     * 是否生成DDl示例文件
     */
    private Boolean generateDDl;
    /**
     * 是否生成DMl示例文件
     */
    private Boolean generateDMl;
    /**
     * 数据源列表
     */
    private List<DataSourceInfo> dataSourceInfos;

    public void generate() {

        if (!generateDDl && !generateDMl) {
            return;
        }

        Map<String, Connection> connectionMap = initDataSourceInfos();
        try {
            List<TableInfo> tableInfos = new ArrayList<>();

            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                Connection connection = entry.getValue();
                List<String> tableList = findAllTableNameList(connection);
                List<TableInfo> tempTableInfos = convertTableInfos(entry.getKey(), connection, tableList);
                tableInfos.addAll(tempTableInfos);
            }

            if (generateDDl) {
                // 引用问题
                List<TableInfo> tempList = CommonUtils.deepCopy(tableInfos);
                log.info("开始生成示例DDL");
                for (TableInfo tableInfo : tempList) {
                    for (TableColumn column : tableInfo.getColumns()) {
                        column.setValueRule(null);
                    }
                }

                FileUtils.writeStringToFile(new File(generateExampleFilePath + "example_ddl.json"), JSONObject.toJSONString(tempList));
            }

            if (generateDMl) {
                // 引用问题
                List<TableInfo> tempList = CommonUtils.deepCopy(tableInfos);
                BusinessDataJson dataJson = new BusinessDataJson();
                List<Scenario> scenarios = new ArrayList<>();
                Scenario scenario = new Scenario();

                for (TableInfo tableInfo : tempList) {
                    tableInfo.setIndexInfos(null);
                    tableInfo.setPrimary(null);
                    tableInfo.setOperateMode(OperateMode.INSERT);

                    for (TableColumn column : tableInfo.getColumns()) {
                        column.setIsNullStr(null);
                        column.setColumnType(null);
                    }
                }
                dataJson.setScenarios(scenarios);
                scenarios.add(scenario);
                scenario.setTableInfos(tempList);

                FileUtils.writeStringToFile(new File(generateExampleFilePath + "example.json"), JSONObject.toJSONString(dataJson));
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new DataSourceException("生成示例文件失败");
        } finally {
            try {
                for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                    entry.getValue().close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private List<TableInfo> convertTableInfos(String dbKey, Connection connection, List<String> tableList) {

        List<TableInfo> tableInfos = new ArrayList<>();
        StringBuilder sb = new StringBuilder("select table_name,column_name,column_comment,column_type,is_nullable,data_type  from information_schema.columns c where table_schema = ? AND table_name IN(");
        for (int i = 0; i < tableList.size(); i++) {
            sb.append("?");

            if (i != tableList.size() - 1) {
                sb.append(",");
            }

            TableInfo tableInfo = new TableInfo();
            tableInfo.setDbKey(dbKey);
            tableInfo.setTableName(tableList.get(i));
            tableInfo.setNo(i + 1);
            tableInfo.setColumns(new ArrayList<>());
            tableInfo.setIndexInfos(new ArrayList<>());
            tableInfos.add(tableInfo);
        }
        sb.append(")");

        try {
            // 填充字段信息
            PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
            preparedStatement.setString(1, connection.getCatalog());
            for (int i = 0; i < tableList.size(); i++) {
                preparedStatement.setString(i + 2, tableList.get(i));
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Map<String, String>> resultList = new ArrayList<>();
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                String columnName = resultSet.getString(2);
                String comment = resultSet.getString(3);
                String columnType = resultSet.getString(4);
                String isNullAble = resultSet.getString(5);
                String dataType = resultSet.getString(6);

                Map<String, String> map = new HashMap<>();
                map.put("tableName", tableName);
                map.put("columnName", columnName);
                map.put("comment", comment);
                map.put("columnType", columnType);
                map.put("isNullAble", isNullAble);
                map.put("dataType", dataType);
                resultList.add(map);
            }

            resultSet.close();

            for (Map<String, String> map : resultList) {

                String tableName = map.get("tableName");
                String columnName = map.get("columnName");
                String comment = map.get("comment");
                String columnType = map.get("columnType");
                String isNullAble = map.get("isNullAble");
                String dataType = map.get("dataType");

                for (TableInfo tableInfo : tableInfos) {
                    if (tableInfo.getTableName().equals(tableName)) {
                        TableColumn tableColumn = new TableColumn();
                        tableColumn.setColumnName(columnName);
                        tableColumn.setComment(comment);
                        tableColumn.setColumnType(columnType);

                        tableColumn.setIsNullStr("YES".equals(isNullAble) ? "NULL" : "NOT NULL");
                        tableColumn.setDataType(SqlTypeEnum.convertSqlType(dataType));
                        ValueRule valueRule = new ValueRule();
                        valueRule.setType("NORMAL");
                        if (SqlTypeEnum.STRING.equals(tableColumn.getDataType())) {
                            valueRule.setDefaultValue("test");
                        } else {
                            valueRule.setDefaultValue(0);
                        }
                        tableColumn.setValueRule(valueRule);
                        tableInfo.getColumns().add(tableColumn);
                    }
                }
            }

            resultList.clear();

            // 填充主键和索引信息
            for (String tableName : tableList) {

                preparedStatement = connection.prepareStatement("show index from " + tableName);
                ResultSet indexResultSet = preparedStatement.executeQuery();
                while (indexResultSet.next()) {
                    String nonUnique = indexResultSet.getString(2);
                    String keyName = indexResultSet.getString(3);
                    String columnName = indexResultSet.getString(5);

                    Map<String, String> map = new HashMap<>();
                    map.put("nonUnique", nonUnique);
                    map.put("keyName", keyName);
                    map.put("columnName", columnName);
                    map.put("tableName", tableName);
                    resultList.add(map);
                }

                indexResultSet.close();
            }

            for (Map<String, String> map : resultList) {
                String nonUnique = map.get("nonUnique");
                String keyName = map.get("keyName");
                String columnName = map.get("columnName");
                String tableName = map.get("tableName");

                for (TableInfo tableInfo : tableInfos) {
                    if (tableInfo.getTableName().equals(tableName)) {
                        if ("0".equals(nonUnique)) {
                            String[] primary = tableInfo.getPrimary();
                            if (primary == null) {
                                tableInfo.setPrimary(new String[]{columnName});
                            } else {
                                String[] newPrimary = new String[primary.length + 1];
                                System.arraycopy(primary, 0, newPrimary, 0, primary.length);
                                newPrimary[primary.length] = columnName;
                                tableInfo.setPrimary(newPrimary);
                            }
                        } else {
                            TableIndex tableIndex = new TableIndex();
                            tableIndex.setIndexName(keyName);
                            tableIndex.setColumnName(columnName);
                            tableInfo.getIndexInfos().add(tableIndex);
                        }
                    }
                }
            }


        } catch (SQLException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new DataSourceException("读取表元数据信息失败");
        }

        return tableInfos;
    }

    /**
     * 初始化数据源列表连接
     *
     * @return
     */
    private Map<String, Connection> initDataSourceInfos() {
        Map<String, Connection> connectionMap = new HashMap<>();
        if (CollectionUtils.isEmpty(dataSourceInfos)) {
            throw new DataSourceException("初始化序列化示例中的数据源列表失败，数据源列表对象dataSourceInfos不能为空");
        }
        for (DataSourceInfo dataSourceInfo : dataSourceInfos) {
            connectionMap.put(dataSourceInfo.getDbKey(), dataSourceInfo.initializeConnection());
        }
        return connectionMap;
    }

    /**
     * 查询数据库中所有表名
     *
     * @param connection
     * @return
     */
    private List<String> findAllTableNameList(Connection connection) {

        List<String> tableNameList = new ArrayList<>();
        try {

            PreparedStatement preparedStatement = connection.prepareStatement("select table_name from information_schema.tables where table_schema=?");
            preparedStatement.setString(1, connection.getCatalog());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tableNameList.add(resultSet.getString(1));
            }

            resultSet.close();

        } catch (SQLException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new DataSourceException("获取数据库所有表名失败");
        }

        return tableNameList;
    }
}
