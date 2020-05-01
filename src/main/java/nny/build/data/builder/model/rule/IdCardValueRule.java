package nny.build.data.builder.model.rule;

import nny.build.data.builder.model.InState;
import nny.build.data.builder.service.IRuleCompute;
import nny.build.data.builder.utils.IdCardUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 身份证
 *
 * @author shengyong.huang
 * @date 2020-02-23
 */
@Getter
@Setter
public class IdCardValueRule extends ValueRule implements IRuleCompute, Serializable {

    private static final long serialVersionUID = -6095360046243725571L;

    @Override
    public Object compute(InState inState) {
        if (this.buildExpressionObject.getExpressionBoolResult()) {
            return IdCardUtils.getIdNo();
        }
        return super.compute(inState);
    }
}
