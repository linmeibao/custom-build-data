package nny.build.data.builder.model.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.enums.SqlTypeEnum;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.model.build.ReferenceDefinition;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;

/**
 * 引用字段
 *
 * @author shengyong.huang
 * @date 2020-02-23
 */
@Slf4j
@Getter
@Setter
public class ReferenceValueRule extends ValueRule implements Serializable {

    private static final long serialVersionUID = 1827996761200994467L;

    /**
     * 引用对象定义
     */
    private ReferenceDefinition refDefinition;

    /**
     * 引用字段表达式
     */
    private String refColumnExpression;

    /**
     * 是否类型格式转换
     */
    private Boolean format = false;

    /**
     * 保留几位小数
     */
    private Integer fewDecimalPlaces;

    @Override
    public Object getRuleValue(InState inState) {

        this.refDefinition = BuildExpression.parseRefColumnExpression(inState, this.refColumnExpression);

        Object value = null;
        if (refDefinition.getCurrentTable()) {
            value = inState.findTableColumnValue(refDefinition.getRefColumnName());
        } else {
            if (CollectionUtils.isEmpty(refDefinition.getRefConditions())) {
                value = inState.findTableColumnValue(refDefinition.getRefNo(), refDefinition.getRefTableName(), refDefinition.getRefColumnName());
            } else {
                value = inState.findTableColumnValue(refDefinition.getRefNo(), refDefinition.getRefTableName(), refDefinition.getRefColumnName(), refDefinition.getRefConditions());
            }
        }

        if (format) {
            SqlTypeEnum sqlTypeEnum = inState.getTableColumn().getDataType();
            switch (sqlTypeEnum) {
                case DOUBLE:
                    value = Double.valueOf(value.toString());
                    if (fewDecimalPlaces != null) {
                        value = Double.valueOf(String.format("%." + fewDecimalPlaces + "f", (Double) value));
                    } else {
                        value = (Double) value;
                    }
                    break;
                default:
                    break;
            }
        }
        return value;
    }
}
