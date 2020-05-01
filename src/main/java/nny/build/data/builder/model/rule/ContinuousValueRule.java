package nny.build.data.builder.model.rule;


import nny.build.data.builder.model.InState;
import nny.build.data.builder.service.IRuleCompute;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 连续值规则
 *
 * @author shengyong.huang
 * @date 2020-02-20
 */
@Getter
@Setter
public class ContinuousValueRule extends ValueRule implements IRuleCompute, Serializable {

    private static final long serialVersionUID = 2928798086490933715L;

    @Override
    public Object compute(InState inState) {
        if (this.buildExpressionObject.getExpressionBoolResult()) {
            return inState.increment();
        }
        return super.compute(inState);
    }
}
