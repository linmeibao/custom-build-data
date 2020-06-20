package nny.build.data.builder.model.rule;

import lombok.Getter;
import lombok.Setter;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.rule.ValueRule;
import nny.build.data.builder.utils.RandomDataUtils;

import java.io.Serializable;

/**
 * 随机电话
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Getter
@Setter
public class RandomMobileValueRule extends ValueRule implements Serializable {

    private static final long serialVersionUID = -2964066123269508064L;

    @Override
    public Object getRuleValue(InState inState) {
        return RandomDataUtils.randomMobilePhone();
    }
}
