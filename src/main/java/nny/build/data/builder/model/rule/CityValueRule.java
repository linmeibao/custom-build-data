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
 * 城市
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Slf4j
@Getter
@Setter
public class CityValueRule extends ValueRule implements Serializable {

    private static final long serialVersionUID = 5747454446539992125L;

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
        return getCity(inState);
    }

    @Override
    protected Map<String, Object> errorMessageMap() {
        return ImmutableMap.of("refColumnExpression", refColumnExpression);
    }

    private String getCity(InState inState) {
        String idCard = null;
        if (refDefinition.getCurrentTable()) {
            idCard = inState.findTableColumnValue(refDefinition.getRefColumnName()).toString();
        } else {
            idCard = inState.findTableColumnValue(refDefinition.getRefNo(), refDefinition.getRefTableName(), refDefinition.getRefColumnName()).toString();
        }
        //取得身份证号，从身份证号中获取城市,身份证前4位
        return idCard.substring(0, 4);
    }
}
