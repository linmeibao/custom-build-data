package nny.build.data.builder.model.rule;

import lombok.Getter;
import lombok.Setter;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.rule.ValueRule;
import nny.build.data.builder.utils.RandomDataUtils;

import java.io.Serializable;

/**
 * 随机数字
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Getter
@Setter
public class RandomNumberValueRule extends ValueRule implements Serializable {

    private static final long serialVersionUID = 4948856655757094800L;

    @Override
    public Object getRuleValue(InState inState) {
        return RandomDataUtils.randomNumber();
    }
}
