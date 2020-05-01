package nny.build.data.builder.model.build;

import nny.build.data.builder.exception.BuilderException;
import nny.build.data.builder.model.table.TableColumn;
import nny.build.data.builder.model.table.TableInfo;
import nny.build.data.builder.model.table.TableRelation;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据构建对象
 *
 * @author shengyong.huang
 * @date 2020-02-25
 */
public class BuildData {

    /**
     * 构建SQL的表对象集合
     */
    List<TableInfo> tableInfos;

    /**
     * Sql语句映射Map
     * dbKey : {"Insert INTO tableName (a,b,c) VALUES(?,?,?)":[[1,2,3],[4,5,6]]}
     * dbKey : {"update tableName set a = ? b= ? c=? where d = ?)":List<List<TableColumn>>}
     */
    Map<String, List<BuildSqlData>> sqlDataMap = new HashMap<>();

    public List<TableInfo> getTableInfos() {
        return tableInfos;
    }

    public void setTableInfos(List<TableInfo> tableInfos) {
        this.tableInfos = tableInfos;
    }

    public Map<String, List<BuildSqlData>> getSqlDataMap() {
        return sqlDataMap;
    }

    /**
     * 填充sqlDataMap
     */
    public void fillSqlMap() {

        if (tableInfos == null) {
            throw new BuilderException("tableInfos不能为空");
        }

        // 将tableInfos转换成sql语句填充到execSqlMap中
        conversionTableInfos(tableInfos);
    }

    /**
     * 合并SQL结果
     *
     * @param buildSqlDataList
     * @param buildSqlData
     * @return
     */
    public List<BuildSqlData> mergeSqlData(List<BuildSqlData> buildSqlDataList, BuildSqlData buildSqlData) {
        boolean bool = false;

        for (BuildSqlData data : buildSqlDataList) {
            if (buildSqlData.getSqlStatement().equals(data.getSqlStatement()) && buildSqlData.getOperateMode().equals(data.getOperateMode())) {
                data.getColumnDataList().addAll(buildSqlData.getColumnDataList());
                bool = true;
            }
        }

        if (!bool) {
            buildSqlDataList.add(buildSqlData);
        }

        return buildSqlDataList;
    }

    private void setSqlData(String dbKey, BuildSqlData buildSqlData) {

        List<BuildSqlData> buildSqlDataList = sqlDataMap.get(dbKey);

        if (buildSqlDataList == null) {
            buildSqlDataList = new ArrayList<>();
            buildSqlDataList.add(buildSqlData);
            sqlDataMap.put(dbKey, buildSqlDataList);
        } else {

            buildSqlDataList = mergeSqlData(buildSqlDataList, buildSqlData);

        }
    }

    private void conversionTableInfos(List<TableInfo> tableInfos) {

        for (TableInfo tableInfo : tableInfos) {

            BuildExpression tableCondition = tableInfo.getTableCondition();

            if (tableCondition != null && !tableCondition.getExpressionBoolResult()) {
                continue;
            }

            BuildSqlData buildSqlData = conversionDml(tableInfo);
            setSqlData(tableInfo.getDbKey(), buildSqlData);

            List<TableRelation> relations = tableInfo.getRelations();

            if (CollectionUtils.isNotEmpty(relations)) {
                conversionTableRelation(relations);
            }

        }

    }


    private void conversionTableRelation(List<TableRelation> relations) {
        for (TableRelation relation : relations) {

            BuildSqlData buildSqlData = conversionDml(relation);
            setSqlData(relation.getRelationTable().getDbKey(), buildSqlData);

        }

    }

    private BuildSqlData conversionDml(TableInfo tableInfo) {

        BuildSqlData buildSqlData = new BuildSqlData();
        buildSqlData.setOperateMode(tableInfo.getOperateMode());
        String sqlStatement = null;

        switch (tableInfo.getOperateMode()) {
            case INSERT:
                sqlStatement = conversionInsertSql(tableInfo.getTableName(), tableInfo.getColumns());
                break;
            case UPDATE:
                sqlStatement = conversionUpdateSql(tableInfo.getTableName(), tableInfo.getColumns(), tableInfo.getWheres());
                break;
        }

        buildSqlData.setSqlStatement(sqlStatement);
        List<List<TableColumn>> columnDataList = new ArrayList<>();

        columnDataList.add(tableInfo.getColumns());
        buildSqlData.setColumnDataList(columnDataList);

        return buildSqlData;
    }

    private BuildSqlData conversionDml(TableRelation tableRelation) {
        BuildSqlData buildSqlData = new BuildSqlData();
        buildSqlData.setOperateMode(tableRelation.getRelationTable().getOperateMode());
        String sqlStatement = null;

        TableInfo tableInfo = tableRelation.getRelationTable();
        List<List<TableColumn>> columnDataList = new ArrayList<>();


        switch (buildSqlData.getOperateMode()) {
            case INSERT:
                sqlStatement = conversionInsertSql(tableInfo.getTableName(), tableInfo.getColumns());
                break;
            case UPDATE:
                sqlStatement = conversionUpdateSql(tableInfo.getTableName(), tableInfo.getColumns(), tableInfo.getWheres());
                break;
        }

        buildSqlData.setSqlStatement(sqlStatement);
        columnDataList.addAll(tableRelation.getRelationColumns());
        buildSqlData.setColumnDataList(columnDataList);

        return buildSqlData;
    }

    private String conversionInsertSql(String tableName, List<TableColumn> columns) {

        StringBuffer sqlBuffer = new StringBuffer("INSERT INTO ");
        sqlBuffer.append(tableName).append(" ");

        sqlBuffer.append("(");
        for (int i = 0; i < columns.size(); i++) {
            TableColumn tableColumn = columns.get(i);
            sqlBuffer.append(tableColumn.getColumnName());

            if (i != columns.size() - 1) {
                sqlBuffer.append(",");
            }
        }
        sqlBuffer.append(") VALUES(");

        for (int i = 0; i < columns.size(); i++) {
            TableColumn tableColumn = columns.get(i);
            sqlBuffer.append("?");
            if (i != columns.size() - 1) {
                sqlBuffer.append(",");
            }
        }
        sqlBuffer.append(")");
        return sqlBuffer.toString();
    }

    private String conversionUpdateSql(String tableName, List<TableColumn> columns, List<TableColumn> wheres) {
        StringBuffer sqlBuffer = new StringBuffer("UPDATE ").append(tableName);
        sqlBuffer.append(" SET ");

        for (int i = 0; i < columns.size(); i++) {
            TableColumn column = columns.get(i);
            sqlBuffer.append(column.getColumnName()).append("=").append("?");

            if (i != columns.size() - 1) {
                sqlBuffer.append(",");
            }
        }

        sqlBuffer.append(" WHERE ");

        for (int i = 0; i < wheres.size(); i++) {
            TableColumn column = wheres.get(i);

            sqlBuffer.append(column.getColumnName()).append("=?");

            if (i != wheres.size() - 1) {
                sqlBuffer.append(" AND ");
            }

        }

        return sqlBuffer.toString();
    }
}
