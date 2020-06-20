package nny.build.data.builder.model.rule;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.model.build.ReferenceDefinition;
import nny.build.data.builder.model.rule.ValueRule;

import java.io.Serializable;
import java.util.Map;

/**
 * 地区
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Slf4j
@Getter
@Setter
public class DistrictValueRule extends ValueRule implements Serializable {
    private static final long serialVersionUID = 8364294457745199482L;

    /**
     * 引用对象定义
     */
    private ReferenceDefinition refDefinition;

    /**
     * 引用字段表达式
     */
    private String refColumnExpression;

    @Override
    public Object getRuleValue(InState inState) {
        this.refDefinition = BuildExpression.parseRefColumnExpression(inState, this.refColumnExpression);
        return getDistrict(inState);
    }

    @Override
    protected Map<String, Object> errorMessageMap() {
        return ImmutableMap.of("refColumnExpression",refColumnExpression);
    }

    private String getDistrict(InState inState) {
        String idCard = null;
        if (refDefinition.getCurrentTable()) {
            idCard = inState.findTableColumnValue(refDefinition.getRefColumnName()).toString();
        } else {
            idCard = inState.findTableColumnValue(refDefinition.getRefNo(), refDefinition.getRefTableName(), refDefinition.getRefColumnName()).toString();
        }
        //取得身份证号，从身份证号中获取区县,身份证前6位
        return idCard.substring(0, 6);
    }
}
