package nny.build.data.builder.model.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.exception.ValueComputeException;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.model.table.TableColumn;
import nny.build.data.builder.model.table.TableInfo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 抽象字段值数据生成规则类
 *
 * @author shengyong.huang
 * @date 2020-06-20
 */
@Slf4j
@Getter
@Setter
public abstract class AbstractValueRule implements Serializable {

    private static final long serialVersionUID = 4569983787941000288L;

    /**
     * 默认值
     */
    protected Object defaultValue;

    /**
     * 表达式
     */
    protected BuildExpression buildExpressionObject;

    /**
     * 规则类型
     */
    protected String type;

    /**
     * 布尔表达式,为true使用计算逻辑，否则使用defaultValue的值（填充buildExpression）
     */
    protected String boolExpression;

    /**
     * 填充布尔表达式
     */
    public void fill(InState inState) {
        if (StringUtils.isNotEmpty(boolExpression)) {
            buildExpressionObject = BuildExpression.parseExpression(inState, boolExpression);
            // 表达式计算
            buildExpressionObject.boolExpressionEvaluation(inState);
        } else {
            buildExpressionObject = new BuildExpression(true);
        }
    }

    /**
     * 字段值规则计算
     *
     * @param inState 中间状态数据
     * @return 结果值
     */
    public final Object compute(InState inState) {
        if (this.buildExpressionObject.getExpressionBoolResult()) {

            TableInfo tableInfo = inState.getTableInfo();
            TableColumn tableColumn = inState.getTableColumn();

            try {
                return this.getRuleValue(inState);
            } catch (Exception e) {
//                if (log.isDebugEnabled()) {
                    e.printStackTrace();
//                }

                Map<String, Object> errorMap = this.errorMessageMap();

                StringBuffer bufferErrorMsg = new StringBuffer("字段生成异常,类型:{");
                bufferErrorMsg.append(this.type).append("} ")
                        .append("tableNo:{").append(tableInfo.getNo()).append("}")
                        .append("tableName:{").append(tableInfo.getTableName()).append("}")
                        .append("columnName:{").append(tableColumn.getColumnName()).append("}");

                if (MapUtils.isNotEmpty(errorMap)) {
                    for (Map.Entry<String, Object> entry : errorMap.entrySet()) {
                        bufferErrorMsg.append(entry.getKey()).append(":{").append(entry.getValue()).append("} ");
                    }
                }
                throw new ValueComputeException(bufferErrorMsg.toString());
            }

        }
        return defaultValue;
    }

    /**
     * 获取规则结果值
     *
     * @param inState 中间状态数据
     * @return 结果值
     */
    protected abstract Object getRuleValue(InState inState);


    /**
     * 错误信息Map对象
     *
     * @return 错误信息
     */
    protected abstract Map<String, Object> errorMessageMap();
}
