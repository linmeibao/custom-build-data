package nny.build.data.builder.model.rule;

import lombok.Getter;
import lombok.Setter;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.utils.IdCardUtils;

import java.io.Serializable;

/**
 * 身份证
 *
 * @author shengyong.huang
 * @date 2020-02-23
 */
@Getter
@Setter
public class IdCardValueRule extends ValueRule implements Serializable {

    private static final long serialVersionUID = -6095360046243725571L;

    @Override
    public Object getRuleValue(InState inState) {
        return IdCardUtils.getIdNo();
    }
}
