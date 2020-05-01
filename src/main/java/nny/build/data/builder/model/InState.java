package nny.build.data.builder.model;

import nny.build.data.builder.exception.ColumnNotFoundException;
import nny.build.data.builder.model.build.BuildCondition;
import nny.build.data.builder.model.table.TableColumn;
import nny.build.data.builder.model.table.TableInStateDefinition;
import nny.build.data.builder.model.table.TableInfo;
import nny.build.data.builder.model.table.TableRelation;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 中间状态数据
 *
 * @author shengyong.huang
 * @date 2020-04-17
 */
@Getter
@Setter
public class InState {

    /**
     * 参与计算的所有场景JSON数据
     */
    private BusinessDataJson businessDataJson;

    /**
     * 当前计算的表
     */
    private TableInfo tableInfo;

    /**
     * 当前计算的列
     */
    private TableColumn tableColumn;

    /**
     * 表内自增值
     */
    private Integer incrementValue;

    /**
     * 表计算过程中间状态变量定义
     */
    private TableInStateDefinition tableInStateDefinition;

    /**
     * 表数据当前对应的行数
     */
    private Integer tableDataRowNum;

    /**
     * 自增
     *
     * @return 自增值
     */
    public Integer increment() {
        return ++this.incrementValue;
    }


    /**
     * 查找字段对象
     *
     * @param refTableNo
     * @param refTableName
     * @param refColumnName
     * @return
     */
    public TableColumn findTableColumn(Integer refTableNo, String refTableName, String refColumnName) {

        return findTableColumn(refTableNo, refTableName, refColumnName, null);

    }


    /**
     * 查找当前表的字段对象
     *
     * @param refColumnName
     * @return
     */
    public TableColumn findTableColumn(String refColumnName) {
        return this.tableInfo.getColumns().stream().filter(col -> refColumnName.equals(col.getColumnName()))
                .findFirst().orElseThrow(() -> new ColumnNotFoundException(String.format("refColumnName 查找失败,依赖字段可能出现配置错误, {%s}", refColumnName)));
    }

    /**
     * 查找字段对象
     *
     * @param refTableNo
     * @param refTableName
     * @param refColumnName
     * @param buildConditions
     * @return
     */
    public TableColumn findTableColumn(Integer refTableNo, String refTableName, String refColumnName, List<BuildCondition> buildConditions) {

        List<TableInfo> tableInfos = new ArrayList<>(15);

        tableInfos = businessDataJson.scenarioTableInfos();

        TableColumn tableColumn = null;

        for (TableInfo tableInfo : tableInfos) {

            if (refTableNo.equals(tableInfo.getNo()) && refTableName.equals(tableInfo.getTableName())) {

                return tableInfo.getColumns().stream().filter(col -> refColumnName.equals(col.getColumnName()))
                        .findFirst().orElseThrow(() -> new ColumnNotFoundException(String.format("refColumnName 查找失败,依赖字段可能出现配置错误, ${%s.%s.%s}", refTableNo, refTableName, refColumnName)));
            } else {
                List<TableRelation> relations = tableInfo.getRelations();

                if (relations != null) {

                    Map<TableInfo, List<List<TableColumn>>> tableInfoListMap = new HashMap<>();

                    for (TableRelation relation : relations) {
                        tableInfoListMap.put(relation.getRelationTable(), relation.getRelationColumns());
                    }

                    tableColumn = recursiveFindTableColumn(refTableNo, refTableName, refColumnName, tableInfoListMap, buildConditions);
                }
            }

            if (tableColumn != null) {
                return tableColumn;
            }
        }

        throw new ColumnNotFoundException(String.format("refColumnName 查找失败,依赖字段可能出现配置错误, ${%s.%s.%s}", refTableNo, refTableName, refColumnName));
    }


    /**
     * 查找字段对象值
     *
     * @param tableNo
     * @param tableName
     * @param columnName
     * @param buildConditions
     * @return
     */
    public Object findTableColumnValue(Integer tableNo, String tableName, String columnName, List<BuildCondition> buildConditions) {
        return findTableColumn(tableNo, tableName, columnName, buildConditions).getColumnValue();
    }

    /**
     * 查找字段值
     *
     * @param refTableNo
     * @param refTableName
     * @param refColumnName
     * @return
     */
    public Object findTableColumnValue(Integer refTableNo, String refTableName, String refColumnName) {
        return findTableColumn(refTableNo, refTableName, refColumnName).getColumnValue();
    }

    /**
     * 查找当前表的字段值
     *
     * @param refColumnName
     * @return
     */
    public Object findTableColumnValue(String refColumnName) {
        return findTableColumn(refColumnName).getColumnValue();
    }

    /**
     * 递归查找字段对象
     *
     * @param refTableNo
     * @param refTableName
     * @param refColumnName
     * @param tableInfoListMap
     * @param buildConditions
     * @return
     */
    private TableColumn recursiveFindTableColumn(Integer refTableNo, String refTableName, String refColumnName, Map<TableInfo, List<List<TableColumn>>> tableInfoListMap, List<BuildCondition> buildConditions) {


        for (Map.Entry<TableInfo, List<List<TableColumn>>> entry : tableInfoListMap.entrySet()) {

            TableInfo tableInfo = entry.getKey();
            List<List<TableColumn>> tableColumnList = entry.getValue();

            if (refTableNo.equals(tableInfo.getNo()) && refTableName.equals(tableInfo.getTableName())) {

                if (CollectionUtils.isEmpty(buildConditions)) {
                    return tableInfo.getColumns().stream().filter(f -> f.getColumnName().equals(refColumnName)).findFirst().orElseThrow(() -> new ColumnNotFoundException(String.format("refColumnName 查找失败,依赖字段可能出现配置错误, {%s %s %s}", refTableNo, refTableName, refColumnName)));
                }

                for (List<TableColumn> tableColumns : tableColumnList) {

                    int matchCount = 0;

                    for (TableColumn column : tableColumns) {

                        for (BuildCondition buildCondition : buildConditions) {
                            if (!buildCondition.getConditionColumn().equals(column.getColumnName())) {
                                continue;
                            }

                            String conditionValue = buildCondition.getConditionColumnValue() == null ? "" : buildCondition.getConditionColumnValue().toString();

                            String columnValue = column.getColumnValue() == null ? "" : column.getColumnValue().toString();

                            if (conditionValue.equals(columnValue)) {
                                matchCount++;
                            }
                        }
                    }

                    if (matchCount == buildConditions.size()) {
                        return tableColumns.stream().filter(f -> f.getColumnName().equals(refColumnName)).findFirst().orElseThrow(() -> new ColumnNotFoundException(String.format("refColumnName 查找失败,依赖字段可能出现配置错误, {%s %s %s}", refTableNo, refTableName, refColumnName)));
                    }
                }

            } else {
                List<TableRelation> relations = tableInfo.getRelations();

                if (relations != null) {

                    Map<TableInfo, List<List<TableColumn>>> tempTableInfoListMap = new HashMap<>();

                    for (TableRelation relation : relations) {
                        tempTableInfoListMap.put(relation.getRelationTable(), relation.getRelationColumns());
                    }

                    return recursiveFindTableColumn(refTableNo, refTableName, refColumnName, tempTableInfoListMap, buildConditions);
                }
            }
        }
        return null;
    }
}
