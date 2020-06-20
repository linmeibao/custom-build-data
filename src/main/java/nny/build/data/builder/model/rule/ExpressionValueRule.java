package nny.build.data.builder.model.rule;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.model.rule.ValueRule;

import java.io.Serializable;
import java.util.Map;

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
public class ExpressionValueRule extends ValueRule implements Serializable {

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
    public Object getRuleValue(InState inState) {
        this.expressionObject = BuildExpression.parseExpression(inState, expression);
        this.expressionObject.expressionEvaluation(inState);
        return expressionObject.getExpressionResult();
    }

    @Override
    protected Map<String, Object> errorMessageMap() {
        return ImmutableMap.of("expression", expression);
    }
}
