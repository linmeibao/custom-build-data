package nny.build.data.builder.model.rule;

import nny.build.data.builder.exception.ValueComputeException;
import nny.build.data.builder.exception.ValueRuleConfigurationException;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.model.build.BuildExpressionParam;
import nny.build.data.builder.model.build.ReferenceDefinition;
import nny.build.data.builder.model.table.TableColumn;
import nny.build.data.builder.model.table.TableInfo;
import nny.build.data.builder.service.IRuleCompute;
import nny.build.data.builder.utils.RandomDataUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 随机日期
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Slf4j
@Getter
@Setter
public class RandomDateConditionValueRule extends ValueRule implements IRuleCompute, Serializable {

    private static final long serialVersionUID = 875493339141611580L;

    /**
     * 比较表达式，当表达式成立时返回生成结果，不成立则不断循环生成结果，直到表达式成立
     * 把当前生成的值不断的放入表达式中进行计算
     */
    private BuildExpression compareExpressionObject;

    /**
     * 比较表达式
     */
    private String compareExpression;

    @Override
    public Object compute(InState inState) {
        if (this.buildExpressionObject.getExpressionBoolResult()) {

            if (StringUtils.isEmpty(compareExpression)) {
                return RandomDataUtils.randomDateString();
            }


            TableInfo tableInfo = inState.getTableInfo();
            TableColumn tableColumn = inState.getTableColumn();

            try {
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
                String value = null;

                while (loop) {
                    value = RandomDataUtils.randomDateString();
                    compareExpressionObject.boolExpressionEvaluation(inState.getTableColumn().getColumnName(), value);
                    loop = !compareExpressionObject.getExpressionBoolResult();
                }

                return value;
            } catch (Exception e) {

                if (log.isDebugEnabled()) {
                    e.printStackTrace();
                }
                throw new ValueComputeException(String.format(
                        "字段生成异常,类型:{%s} tableNo:{%s},tableName:{%s},columnName:{%s} expression:'%s'",
                        this.type, tableInfo.getNo(), tableInfo.getTableName(), tableColumn.getColumnName(), this.compareExpression));

            }
        }
        return super.compute(inState);

    }


}
