package nny.build.data.builder.model.rule;


import lombok.Getter;
import lombok.Setter;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.rule.ValueRule;

import java.io.Serializable;

/**
 * 连续值规则
 *
 * @author shengyong.huang
 * @date 2020-02-20
 */
@Getter
@Setter
public class ContinuousValueRule extends ValueRule implements Serializable {

    private static final long serialVersionUID = 2928798086490933715L;

    @Override
    public Object getRuleValue(InState inState) {
        return inState.increment();
    }
}
