package nny.build.data.builder.model.rule;

import nny.build.data.builder.model.InState;
import nny.build.data.builder.service.IRuleCompute;
import nny.build.data.builder.utils.RandomDataUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 随机电话
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Getter
@Setter
public class RandomMobileValueRule extends ValueRule implements IRuleCompute, Serializable {

    private static final long serialVersionUID = -2964066123269508064L;

    @Override
    public Object compute(InState inState) {
        if (this.buildExpressionObject.getExpressionBoolResult()) {

            return RandomDataUtils.randomMobilePhone();
        }
        return super.compute(inState);
    }
}
