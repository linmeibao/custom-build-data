package nny.build.data.builder.model.rule;

import nny.build.data.builder.model.InState;
import nny.build.data.builder.service.IRuleCompute;
import nny.build.data.builder.utils.RandomDataUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 随机数字
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Getter
@Setter
public class RandomNumberValueRule extends ValueRule implements IRuleCompute, Serializable {

    private static final long serialVersionUID = 4948856655757094800L;

    @Override
    public Object compute(InState inState) {
        if (this.buildExpressionObject.getExpressionBoolResult()) {
            return RandomDataUtils.randomNumber();
        }
        return super.compute(inState);
    }
}
