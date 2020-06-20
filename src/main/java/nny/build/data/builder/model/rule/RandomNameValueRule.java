package nny.build.data.builder.model.rule;

import lombok.Getter;
import lombok.Setter;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.utils.RandomDataUtils;

import java.io.Serializable;

/**
 * 随机名字
 *
 * @author shengyong.huang
 * @date 2020-02-23
 */
@Getter
@Setter
public class RandomNameValueRule extends ValueRule implements Serializable {

    private static final long serialVersionUID = 4040792250491512117L;

    @Override
    public Object getRuleValue(InState inState) {
        return RandomDataUtils.randomName();
    }
}
