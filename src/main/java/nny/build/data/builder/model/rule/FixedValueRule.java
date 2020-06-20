package nny.build.data.builder.model.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.exception.ValueRuleConfigurationException;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.rule.ValueRule;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 固定值规则计算
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Slf4j
@Getter
@Setter
public class FixedValueRule extends ValueRule implements Serializable {

    private static final long serialVersionUID = -7980707928078951140L;

    /**
     * 表内连续值标识
     */
    private static final String CONTINUOUS_VALUE = "CONTINUOUS_VALUE";

    @Override
    public Object getRuleValue(InState inState) {

        if (inState.getTableInStateDefinition() == null) {
            throw new ValueRuleConfigurationException("当前tableInfo缺少配置tableInStateDefinition属性, 无法进行固定值计算");
        }

        if (MapUtils.isEmpty(inState.getTableInStateDefinition().getTableFixedValues())) {
            throw new ValueRuleConfigurationException("当前tableInfo属性的tableInStateDefinition中缺少配置tableFixedValues, 无法进行固定值计算");
        }

        Map<String, Object[]> tableFixValues = inState.getTableInStateDefinition().getTableFixedValues();
        String columnName = inState.getTableColumn().getColumnName();

        int tableDataRowNum = inState.getTableDataRowNum();

        Object[] fixValueArray = tableFixValues.get(columnName);

        if (tableDataRowNum >= fixValueArray.length) {
            Map<String, Object> fixedPlaceholder = inState.getTableInStateDefinition().getFixedPlaceholder();

            if (MapUtils.isEmpty(fixedPlaceholder)) {
                throw new ValueRuleConfigurationException("当前tableInfo属性的tableInStateDefinition中缺少配置fixedPlaceholder,当前行号已经超出了tableFixedValues的长度，无法进行固定值计算");
            }

            Object value = fixedPlaceholder.get(columnName);

            if (CONTINUOUS_VALUE.equals(value)) {
                return inState.increment();
            } else {
                return value;
            }

        }

        return tableFixValues.get(columnName)[tableDataRowNum];
    }
}
