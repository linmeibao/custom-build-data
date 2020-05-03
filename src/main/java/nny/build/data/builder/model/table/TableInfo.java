package nny.build.data.builder.model.table;


import nny.build.data.builder.enums.OperateMode;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.service.IBuildCompute;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 表对象
 *
 * @author shengyong.huang
 * @date 2020-02-24
 **/
@Getter
@Setter
public class TableInfo implements IBuildCompute, Serializable {

    private static final long serialVersionUID = 1670187134738040606L;

    /**
     * table编号
     */
    private Integer no;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 数据源key
     */
    private String dbKey;

    /**
     * 生成表数据时需要满足的条件
     */
    private BuildExpression tableCondition;

    /**
     * 条件表达式（填充tableCondition）
     */
    private String conditionExpression;

    /**
     * 操作类型
     */
    private OperateMode operateMode;

    /**
     * 表数据计算过程中间状态变量定义
     */
    private TableInStateDefinition tableInStateDefinition;

    /**
     * 字段定义列表
     */
    private List<TableColumn> columns = new ArrayList<>();

    /**
     * 条件定义列表
     */
    private List<TableColumn> wheres = new ArrayList<>();

    /**
     * 关联表列表
     */
    private List<TableRelation> relations;

    /*DML时可省略的属性*/

    /**
     * 索引列表
     */
    private List<TableIndex> indexInfos;

    /**
     * 主键列表
     */
    private String[] primary;

    /**
     * 描述
     */
    private String desc;

    @Override
    public void initialize(InState inState) {

        // tableCondition表达式计算
        if (StringUtils.isNotEmpty(conditionExpression)) {
            tableCondition = BuildExpression.parseExpression(inState, conditionExpression);
            tableCondition.boolExpressionEvaluation(inState);
        }

        // 填充tableInStateDefinition
        if (tableInStateDefinition != null) {
            tableInStateDefinition.fill(inState);
        }

        // 将当前计算表对象 设置到中间状态对象中
        inState.setTableInfo(this);
    }

    @Override
    public void build(InState inState) {

        if (tableCondition != null && !tableCondition.getExpressionBoolResult()) {
            return;
        }

        for (TableColumn column : this.getColumns()) {
            column.initialize(inState);
            column.build(inState);
        }

        for (TableColumn where : this.getWheres()) {
            where.initialize(inState);
            where.build(inState);
        }

        if (relations != null) {

            TableInStateDefinition tempDef = inState.getTableInStateDefinition();
            Integer incrementValue = inState.getIncrementValue();
            Integer tableDataRowNum = inState.getTableDataRowNum();

            for (TableRelation relation : relations) {
                relation.initialize(inState);
                relation.build(inState);
            }

            inState.setTableInStateDefinition(tempDef);
            inState.setIncrementValue(incrementValue);
            inState.setTableDataRowNum(tableDataRowNum);
        }

    }

}
