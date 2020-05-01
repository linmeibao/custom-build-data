package nny.build.data.builder.model.rule;

import nny.build.data.builder.model.InState;
import nny.build.data.builder.service.IRuleCompute;
import nny.build.data.builder.utils.RandomDataUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 随机字符串规则
 *
 * @author shengyong.huang
 * @date 2020-02-19
 */
@Getter
@Setter
public class RandomStringValueRule extends ValueRule implements IRuleCompute, Serializable {

    private static final long serialVersionUID = -6679182441833362377L;

    /**
     * 字符串前缀
     */
    private String prefix = "";

    @Override
    public Object compute(InState inState) {
        if (this.buildExpressionObject.getExpressionBoolResult()) {
            return this.prefix + RandomDataUtils.randomString();
        }
        return super.compute(inState);
    }
}
