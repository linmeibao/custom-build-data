package nny.build.data.builder.model.rule;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.model.build.ReferenceDefinition;

import java.io.Serializable;
import java.util.Map;

/**
 * 生日
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Slf4j
@Getter
@Setter
public class BirthValueRule extends ValueRule implements Serializable {
    private static final long serialVersionUID = -7200175806529308023L;

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
        return getBirth(inState);
    }

    @Override
    protected Map<String, Object> errorMessageMap() {
        return ImmutableMap.of("refColumnExpression", refColumnExpression);
    }

    private String getBirth(InState inState) {
        StringBuilder birth = new StringBuilder();
        String idCard = null;
        if (refDefinition.getCurrentTable()) {
            idCard = inState.findTableColumnValue(refDefinition.getRefColumnName()).toString();
        } else {
            idCard = inState.findTableColumnValue(refDefinition.getRefNo(), refDefinition.getRefTableName(), refDefinition.getRefColumnName()).toString();
        }

        //年份
        birth.append(idCard.substring(6, 10));
        birth.append("-");
        //月份
        birth.append(idCard.substring(10, 12));
        birth.append("-");
        //日
        birth.append(idCard.substring(12, 14));

        return birth.toString();
    }
}
