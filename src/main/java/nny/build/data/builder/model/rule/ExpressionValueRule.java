package nny.build.data.builder.model.rule;

import nny.build.data.builder.exception.ValueComputeException;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.model.table.TableColumn;
import nny.build.data.builder.model.table.TableInfo;
import nny.build.data.builder.service.IRuleCompute;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * EXPRESSION
 * 表达式计算
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Slf4j
@Getter
@Setter
public class ExpressionValueRule extends ValueRule implements IRuleCompute, Serializable {

    private static final long serialVersionUID = -1762745790042140599L;

    /**
     * 表达式对象
     */
    private BuildExpression expressionObject;

    /**
     * 表达式
     */
    private String expression;

    @Override
    public Object compute(InState inState) {

        TableInfo tableInfo = inState.getTableInfo();
        TableColumn tableColumn = inState.getTableColumn();

        try {
            if (this.buildExpressionObject.getExpressionBoolResult()) {
                this.expressionObject = BuildExpression.parseExpression(inState, expression);
                this.expressionObject.expressionEvaluation(inState);
                return expressionObject.getExpressionResult();
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ValueComputeException(String.format(
                    "字段生成异常,类型:{%s} tableNo:{%s},tableName:{%s},columnName:{%s} expression:'%s'",
                    this.type, tableInfo.getNo(), tableInfo.getTableName(), tableColumn.getColumnName(),
                    this.expression));
        }
        return super.compute(inState);
    }
}
