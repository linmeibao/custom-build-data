package nny.build.data.builder.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nny.build.data.builder.config.BuilderConfig;
import nny.build.data.builder.exception.BuilderException;
import nny.build.data.builder.model.BusinessDataJson;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildData;
import nny.build.data.builder.model.build.BuildSqlData;
import nny.build.data.builder.model.table.TableColumn;
import nny.build.data.builder.model.table.TableIndex;
import nny.build.data.builder.model.table.TableInfo;
import nny.build.data.builder.model.table.TableRelation;
import nny.build.data.builder.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 构建数据服务
 *
 * @author shengyong.huang
 * @date 2020-02-25
 */
@Slf4j
public class BuilderService {

    private IJdbcService jdbcService;

    public BuilderService(IJdbcService jdbcService) {
        this.jdbcService = jdbcService;
    }


    /**
     * 加载业务Json文件
     *
     * @param dataJsonPath 文件地址
     * @return 业务数据对象
     */
    public BusinessDataJson loadJsonFile(String dataJsonPath) {

        String content = null;
        try {
            content = FileUtils.readFileToString(new File(dataJsonPath), "UTF-8");
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BuilderException(String.format("数据配置文件不存在, 检查路径:{%s}", dataJsonPath));
        }

        ObjectMapper objectMapper = new ObjectMapper();

        BusinessDataJson businessDataJson = null;
        try {
            businessDataJson = objectMapper.readValue(content, new TypeReference<BusinessDataJson>() {
            });
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BuilderException(String.format("数据配置反序列化失败, 请检查配置文件, 异常信息:{%s}", e.getMessage()));
        }

        return businessDataJson;
    }

    /**
     * 加载DDL Json文件
     *
     * @param dataDDLJsonPath 文件地址
     * @return 表数据对象
     */
    public List<TableInfo> loadDDlJsonFile(String dataDDLJsonPath) {

        String content = null;
        try {
            content = FileUtils.readFileToString(new File(dataDDLJsonPath), "UTF-8");
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BuilderException(String.format("数据自动删表建表配置文件不存在, 检查路径:{%s}", dataDDLJsonPath));
        }

        ObjectMapper objectMapper = new ObjectMapper();

        List<TableInfo> tableInfos = null;
        try {
            tableInfos = objectMapper.readValue(content, new TypeReference<List<TableInfo>>() {
            });
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BuilderException(String.format("数据建表配置反序列化失败, 请检查配置文件, 异常信息:{%s}", e.getMessage()));
        }

        return tableInfos;
    }


    /**
     * 构建Sql语句映射Map对象
     *
     * @param buildDataList 数据构建对象集合
     * @return dbkey:[insert语句,update语句]
     */
    public Map<String, List<BuildSqlData>> buildSqlDataMap(List<BuildData> buildDataList) {

        Map<String, List<BuildSqlData>> sqlDataMap = new HashMap<>();

        for (BuildData buildData : buildDataList) {
            Map<String, List<BuildSqlData>> builderDataSqlMap = buildData.getSqlDataMap();

            for (Map.Entry<String, List<BuildSqlData>> entry : builderDataSqlMap.entrySet()) {

                List<BuildSqlData> buildSqlDataList = sqlDataMap.get(entry.getKey());

                if (buildSqlDataList == null) {
                    buildSqlDataList = entry.getValue();
                    sqlDataMap.put(entry.getKey(), buildSqlDataList);
                } else {

                    for (BuildSqlData data : entry.getValue()) {

                        buildSqlDataList = buildData.mergeSqlData(buildSqlDataList, data);
                    }
                }

            }
        }
        return sqlDataMap;
    }

    /**
     * 构建SQL语句输出到日志文件
     *
     * @param sqlDataMap sql语句封装对象
     * @param filePath   日志文件地址
     */
    public void buildSqlNoteFile(Map<String, List<BuildSqlData>> sqlDataMap, String filePath) {
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, List<BuildSqlData>> entry : sqlDataMap.entrySet()) {
            buffer.append("-- ").append(entry.getKey()).append("\n");

            for (BuildSqlData buildSqlData : entry.getValue()) {

                String sqlStatement = buildSqlData.getSqlStatement();

                if (sqlStatement.startsWith("INSERT")) {

                    sqlStatement = sqlStatement.substring(0, sqlStatement.indexOf("VALUES"));

                    buffer.append(sqlStatement).append(" \nVALUES(");

                    for (int j = 0; j < buildSqlData.getColumnDataList().size(); j++) {
                        List<TableColumn> tableColumns = buildSqlData.getColumnDataList().get(j);

                        if (j > 0) {
                            buffer.append("(");
                        }

                        for (int i = 0; i < tableColumns.size(); i++) {

                            TableColumn tableColumn = tableColumns.get(i);

                            Object value = tableColumn.getColumnValue();

                            switch (tableColumn.getDataType()) {
                                case STRING:
                                case DATE:
                                case TIMESTAMP:
                                    if (value == null) {
                                        buffer.append(value);
                                    } else {
                                        buffer.append("'").append(value).append("'");
                                    }
                                    break;
                                default:
                                    buffer.append(tableColumn.getColumnValue());
                                    break;
                            }

                            if (i != tableColumns.size() - 1) {
                                buffer.append(",");
                            }
                        }

                        if (j != buildSqlData.getColumnDataList().size() - 1) {
                            buffer.append("),\n");
                        }
                    }
                    buffer.append(");").append("\n\n");
                } else {
                    String[] sqlArrays = sqlStatement.split("\\?");

                    List<List<TableColumn>> columnDataList = buildSqlData.getColumnDataList();

                    for (List<TableColumn> tableColumns : columnDataList) {

                        for (int i = 0; i < tableColumns.size(); i++) {

                            buffer.append(sqlArrays[i]);

                            TableColumn tableColumn = tableColumns.get(i);
                            switch (tableColumn.getDataType()) {
                                case STRING:
                                case DATE:
                                case TIMESTAMP:
                                    buffer.append("'").append(tableColumn.getColumnValue()).append("' ");
                                    break;
                                default:
                                    buffer.append(tableColumn.getColumnValue());
                                    break;
                            }
                        }
                        buffer.append("; \n\n");
                    }
                }
            }
        }

        try {
            String fileName = filePath + "sql_note_" + Thread.currentThread().getName() + "_" + System.currentTimeMillis() + ".sql";
            FileUtils.writeStringToFile(new File(fileName), buffer.toString(), "UTF-8");
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BuilderException("生成SQL日志文件失败");
        }
    }

    /**
     * 构建建表语句
     *
     * @param tableInfos 表集合
     * @return DDL语句map集合
     */
    public Map<String, List<String>> buildDDl(List<TableInfo> tableInfos) {

        List<String> tableNameList = new ArrayList<>();

        String sql = null;

        // 数据源key:[{ddl},{ddl}]
        // DDL语句map集合
        Map<String, List<String>> ddlSqlMap = new HashMap<>();

        // 关联表对象
        TableInfo relationTableInfo = null;

        // 存储数据源的key
        for (TableInfo tableInfo : tableInfos) {
            ddlSqlMap.put(tableInfo.getDbKey(), null);

            if (CollectionUtils.isEmpty(tableInfo.getRelations())) {
                continue;
            }

            for (TableRelation tableRelation : tableInfo.getRelations()) {
                ddlSqlMap.put(tableRelation.getRelationTable().getDbKey(), null);
            }
        }

        //初始化map中每个key的value
        for (Map.Entry<String, List<String>> entry : ddlSqlMap.entrySet()) {
            entry.setValue(new ArrayList<>());
        }

        //根据key保存对应的SQL语句，每个dbKey的value是该dbKey对应数据库的所有需要执行的建表语句，每个建表语句是一条String
        for (TableInfo tableInfo : tableInfos) {

            //表已在数据库中存在则先drop
            String dropSql = dropTable(tableInfo.getTableName());

            //取出当前循环的dbKey(主表)
            String dbKey = tableInfo.getDbKey();

            //如果表名未存在
            if (!tableNameList.contains(tableInfo.getTableName())) {

                //创建主表
                sql = createTable(tableInfo.getTableName(), tableInfo.getColumns(), tableInfo.getPrimary(), tableInfo.getIndexInfos());
                // 取出当前主表dbKey的List(value)
                List<String> sqlList = ddlSqlMap.get(dbKey);

                //先add drop语句
                sqlList.add(dropSql);
                // add建表语句
                sqlList.add(sql);

                //将表名add到tableNameList中用于进行下一次循环判断
                tableNameList.add(tableInfo.getTableName());

            }

            //如果关联表对象不为null则创建关联表
            if (tableInfo.getRelations() != null) {

                String relationDbKey = null;
                //获取关联表信息
                for (TableRelation relation : tableInfo.getRelations()) {

                    relationTableInfo = relation.getRelationTable();

                    if (tableNameList.contains(relationTableInfo.getTableName())) {
                        continue;
                    }

                    //表已在数据库中存在则先drop
                    String relationDropSql = dropTable(relationTableInfo.getTableName());

                    //获取关联表的dbKey
                    relationDbKey = relationTableInfo.getDbKey();

                    //创建关联表
                    sql = createTable(relationTableInfo.getTableName(), relationTableInfo.getColumns(), relationTableInfo.getPrimary(), relationTableInfo.getIndexInfos());

                    // 取出当前关联表dbKey的List(value)
                    List<String> relationSqlList = ddlSqlMap.get(relationDbKey);
                    //先add drop语句
                    relationSqlList.add(relationDropSql);
                    // add建表语句
                    relationSqlList.add(sql);

                    tableNameList.add(relationTableInfo.getTableName());

                }
            }
        }

        return ddlSqlMap;
    }

    /**
     * 拼接建表语句
     *
     * @param tableName  表名
     * @param columns    列名
     * @param primary    主键
     * @param indexInfos 索引
     */
    private String createTable(String tableName, List<TableColumn> columns, String[] primary, List<TableIndex> indexInfos) {
        StringBuilder sql = new StringBuilder("");
        sql.append("CREATE TABLE ");
        sql.append(tableName);
        sql.append(" (");

        for (TableColumn column : columns) {
            // 字段
            sql.append(column.getColumnName());
            sql.append(" ");
            sql.append(column.getColumnType());
            sql.append(" ");
            sql.append(column.getIsNullStr());
            if (StringUtils.isNotEmpty(column.getComment())) {
                sql.append(" COMMENT ");
                sql.append("'");
                sql.append(column.getComment());
                sql.append("'");
            }
            sql.append(",");
        }


        if (primary != null) {
            // 主键
            sql.append("PRIMARY KEY (");
            for (int i = 0; i < primary.length; i++) {
                if (i != primary.length - 1) {
                    sql.append(primary[i]);
                    sql.append(",");
                } else {
                    sql.append(primary[i]);
                    if (CollectionUtils.isNotEmpty(indexInfos)) {
                        sql.append("),");
                    } else {
                        sql.append("))");
                    }
                }
            }
        }


        if (CollectionUtils.isNotEmpty(indexInfos)) {
            // 索引
            for (TableIndex index : indexInfos) {
                sql.append("KEY ");
                sql.append(index.getIndexName());
                sql.append(" ");
                sql.append("(");
                sql.append(index.getColumnName());
                sql.append(")");
                //判断是否是最后一个需要设置的索引
                if (!index.equals(indexInfos.get(indexInfos.size() - 1))) {
                    sql.append(",");
                } else {
                    sql.append(")");
                }
            }
        }

        if (primary == null && CollectionUtils.isEmpty(indexInfos)) {
            sql.deleteCharAt(sql.length() - 1);
        }

        return sql.toString();
    }

    /**
     * 拼接删除表SQL
     *
     * @param tableName 表名称
     * @return 删表语句
     */
    private String dropTable(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }


    /**
     * 构建数据
     *
     * @param businessDataJson 业务数据json对象
     * @param batchSize        批量大小
     * @return 数据构建对象集合
     */
    public List<BuildData> build(BusinessDataJson businessDataJson, Integer batchSize) {

        List<BuildData> buildDataList = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {

            BusinessDataJson baseDataJson = CommonUtils.deepCopy(businessDataJson);
            // 深拷贝之后 再选择场景避免引用问题
            baseDataJson.selectScene();

            List<TableInfo> tableInfos = new ArrayList<>();

            InState inState = new InState();
            inState.setBusinessDataJson(baseDataJson);

            for (TableInfo tableInfo : baseDataJson.scenarioTableInfos()) {
                tableInfo.initialize(inState);
                tableInfo.build(inState);
                tableInfos.add(tableInfo);
            }

            BuildData buildData = new BuildData();
            buildData.setTableInfos(tableInfos);
            buildDataList.add(buildData);
        }


        return buildDataList;
    }


    /**
     * 构建完成的数据进行入库
     *
     * @param buildDataList 构建数据对象集合
     * @param builderConfig 配置文件
     */
    public void storage(List<BuildData> buildDataList, BuilderConfig builderConfig) {
        // 填充sqlDataMap
        buildDataList.forEach(BuildData::fillSqlMap);

        // 构建SQLData
        Map<String, List<BuildSqlData>> sqlDataMap = buildSqlDataMap(buildDataList);


        buildSqlNoteFile(sqlDataMap, this.getSqlNoteFolderPath(builderConfig));

        boolean result = jdbcService.executeDml(builderConfig.getDataSourceConfig(), sqlDataMap);

        if (!result) {
            throw new BuilderException(String.format("DML执行失败, {%s}", JSONObject.toJSONString(sqlDataMap)));
        }
    }

    public void mergeSqlNote(String folderPath) {

        File folder = new File(folderPath);

        if (!folder.isDirectory()) {
            throw new BuilderException(String.format("{%s}不是一个文件目录, 无法进行合并", folderPath));
        }

        File[] files = folder.listFiles();
        if (files == null) {
            throw new BuilderException(String.format("{%s}文件目录下没有文件, 无法进行合并", folderPath));
        }

        File curFile = null;

        StringBuilder sb = new StringBuilder();

        try {
            for (File file : files) {
                curFile = file;

                String content = FileUtils.readFileToString(file, "UTF-8");
                file.delete();
                sb.append(content);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BuilderException(String.format("文件夹{%s}的文件{%s}读取失败", folderPath, curFile));
        }


        String finalFileName = folderPath + File.separator + "sql_note.sql";

        try {
            FileUtils.writeStringToFile(new File(finalFileName), sb.toString(), "UTF-8");
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BuilderException(String.format("最终的SQL日志文件{%s}写入失败", finalFileName));
        }

    }

    public String getSqlNoteFolderPath(BuilderConfig builderConfig) {
        String filePath = builderConfig.getSqlFileOutputFilePath();
        String todayStr = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return filePath + todayStr + File.separator;
    }
}
