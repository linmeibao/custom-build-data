package nny.build.data.builder.model.rule;

import nny.build.data.builder.model.InState;
import nny.build.data.builder.service.IRuleCompute;
import nny.build.data.builder.utils.RandomDataUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 随机名字
 *
 * @author shengyong.huang
 * @date 2020-02-23
 */
@Getter
@Setter
public class RandomNameValueRule extends ValueRule implements IRuleCompute, Serializable {

    private static final long serialVersionUID = 4040792250491512117L;

    @Override
    public Object compute(InState inState) {
        if (this.buildExpressionObject.getExpressionBoolResult()) {
            return RandomDataUtils.randomName();
        }
        return super.compute(inState);
    }
}
