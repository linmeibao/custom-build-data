package nny.build.data.builder.model.rule;

import nny.build.data.builder.model.InState;
import nny.build.data.builder.service.IRuleCompute;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;

/**
 * 随机选择器规则
 *
 * @author shengyong.huang
 * @date 2020-02-19
 */
@Getter
@Setter
public class RandomSelectorValueRule extends ValueRule implements IRuleCompute, Serializable {

    private static final long serialVersionUID = -6013730955223186854L;

    /**
     * 随机值数组
     */
    private Object[] valueArray;

    @Override
    public Object compute(InState inState) {
        if (this.buildExpressionObject.getExpressionBoolResult() && valueArray != null) {
            return valueArray[RandomUtils.nextInt(0, valueArray.length)];
        }
        return super.compute(inState);
    }
}
