package nny.build.data.builder.model.table;


import nny.build.data.builder.enums.SqlTypeEnum;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.rule.ValueRule;
import nny.build.data.builder.service.IBuildCompute;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 表字段对象
 *
 * @author shengyong.huang
 * @date 2020-02-24
 **/
@Getter
@Setter
public class TableColumn implements IBuildCompute, Serializable {

    private static final long serialVersionUID = 8631722743516351234L;

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 数据类型
     */
    private SqlTypeEnum dataType;

    /**
     * 注释说明
     */
    private String comment;

    /**
     * 字段生成规则
     */
    private ValueRule valueRule;

    /**
     * 字段结果值
     */
    private Object columnValue;

    /*DML时可省略的属性*/

    /**
     * 字段类型
     */
    private String columnType;

    /**
     * 是否默认为空
     */
    private String isNullStr;


    @Override
    public void initialize(InState inState) {
        // 将当前计算表字段 设置到中间状态对象中
        inState.setTableColumn(this);
        // 填充valueRule
        valueRule.fill(inState);
    }

    @Override
    public void build(InState inState) {
        // 规则计算
        this.columnValue = valueRule.compute(inState);
    }
}
