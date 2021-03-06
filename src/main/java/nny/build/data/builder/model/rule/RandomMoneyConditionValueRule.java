package nny.build.data.builder.model.rule;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.exception.ValueRuleConfigurationException;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.model.build.BuildExpressionParam;
import nny.build.data.builder.model.build.ReferenceDefinition;
import nny.build.data.builder.utils.RandomDataUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 随机金额附带条件规则
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Slf4j
@Getter
@Setter
public class RandomMoneyConditionValueRule extends ValueRule implements Serializable {

    private static final long serialVersionUID = 4465622463522379471L;

    /**
     * 比较表达式对象，当表达式成立时返回生成结果，不成立则不断循环生成结果，直到表达式成立
     * 把当前生成的值不断的放入表达式中进行计算
     */
    private BuildExpression compareExpressionObject;

    /**
     * 比较表达式
     */
    private String compareExpression;


    @Override
    public Object getRuleValue(InState inState) {

        if (StringUtils.isEmpty(compareExpression)) {
            return RandomDataUtils.numberFormat(RandomUtils.nextInt(5000, 35001));
        }


        // 比较字段名称
        String compareColumnName = null;

        // 解析表达式
        compareExpressionObject = BuildExpression.parseExpression(inState, compareExpression);

        // 填充表达式参数
        List<BuildExpressionParam> params = compareExpressionObject.getBuildExpressionParams();

        for (BuildExpressionParam param : params) {
            ReferenceDefinition refDefinition = param.getRefDefinition();
            if (!inState.getTableColumn().getColumnName().equals(refDefinition.getRefColumnName())) {
                compareColumnName = refDefinition.getRefColumnName();
            }
            // 开始填充参数
            param.fillParamValue(inState);
        }

        if (StringUtils.isEmpty(compareColumnName)) {
            throw new ValueRuleConfigurationException(String.format("未找到比较字段名称,字段配置错误 {%s}", compareExpression));
        }

        // 比较值
        Object compareColumnValue = null;
        for (BuildExpressionParam param : params) {
            ReferenceDefinition refDefinition = param.getRefDefinition();
            if (refDefinition.getRefColumnName().equals(compareColumnName)) {
                compareColumnValue = param.getParamValue();
            }
        }

        if (compareColumnValue == null) {
            throw new ValueRuleConfigurationException(String.format("未找到compareColumnName对应的结果值或者值为NULL,比较字段 {%s}", compareColumnName));
        }

        boolean loop = true;
        Integer value = null;

        while (loop) {
            value = RandomUtils.nextInt(1000, (Integer) compareColumnValue);
            compareExpressionObject.boolExpressionEvaluation(inState.getTableColumn().getColumnName(), value);
            loop = !compareExpressionObject.getExpressionBoolResult();
        }

        return RandomDataUtils.numberFormat(value);
    }


    @Override
    protected Map<String, Object> errorMessageMap() {
        return ImmutableMap.of("compareExpressionObject", compareExpressionObject);
    }
}
