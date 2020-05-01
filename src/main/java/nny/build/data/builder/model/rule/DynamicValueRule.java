package nny.build.data.builder.model.rule;

import nny.build.data.builder.exception.ValueComputeException;
import nny.build.data.builder.exception.ValueRuleConfigurationException;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.table.TableColumn;
import nny.build.data.builder.model.table.TableInfo;
import nny.build.data.builder.service.IRuleCompute;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 动态值规则
 *
 * @author shengyong.huang
 * @date 2020-04-13
 */
@Slf4j
@Getter
@Setter
public class DynamicValueRule extends ValueRule implements IRuleCompute, Serializable {
    private static final long serialVersionUID = -7034605051113618291L;

    @Override
    public Object compute(InState inState) {
        TableInfo tableInfo = inState.getTableInfo();
        TableColumn tableColumn = inState.getTableColumn();

        try {
            if (this.buildExpressionObject.getExpressionBoolResult()) {

                if (inState.getTableInStateDefinition() == null) {
                    throw new ValueRuleConfigurationException("当前tableInfo缺少配置tableInStateDefinition属性, 无法进行动态值计算");
                }

                if (MapUtils.isEmpty(inState.getTableInStateDefinition().getTableDynamicValues())) {
                    throw new ValueRuleConfigurationException("当前tableInfo属性的tableInStateDefinition中缺少配置tableDynamicValues, 无法进行动态值计算");
                }

                Map<String, List<TableColumn>> tableDynamicValues = inState.getTableInStateDefinition().getTableDynamicValues();
                String columnName = inState.getTableColumn().getColumnName();

                int tableDataRowNum = inState.getTableDataRowNum();
                List<TableColumn> tableDynamicValueList = tableDynamicValues.get(columnName);

                if (tableDataRowNum >= tableDynamicValueList.size()) {
                    Map<String, TableColumn> dynamicPlaceholder = inState.getTableInStateDefinition().getDynamicPlaceholder();
                    if (MapUtils.isEmpty(dynamicPlaceholder)) {
                        throw new ValueRuleConfigurationException("当前tableInfo属性的tableInStateDefinition中缺少配置dynamicPlaceholder,当前行号已经超出了tableDynamicValues的长度，无法进行动态值计算");
                    }
                    TableColumn value = dynamicPlaceholder.get(columnName);
                    return value.getColumnValue();
                }

                return tableDynamicValues.get(columnName).get(tableDataRowNum).getColumnValue();

            }

        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ValueComputeException(String.format(
                    "字段生成异常,类型:{%s} tableNo:{%s},tableName:{%s},columnName:{%s}",
                    this.type, tableInfo.getNo(), tableInfo.getTableName(), tableColumn.getColumnName()));
        }
        return super.compute(inState);
    }
}
