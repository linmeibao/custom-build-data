package nny.build.data.builder.model.build;

import nny.build.data.builder.model.InState;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;

/**
 * 构建表达式参数
 *
 * @author shengyong.huang
 * @date 2020-04-17
 */
@Getter
@Setter
public class BuildExpressionParam implements Serializable {

    private static final long serialVersionUID = -5891606130567298523L;


    public BuildExpressionParam(ReferenceDefinition referenceDefinition) {
        this.refDefinition = referenceDefinition;
    }

    public BuildExpressionParam() {
    }


    /**
     * 引用对象定义
     */
    private ReferenceDefinition refDefinition;

    /**
     * 参数值
     */
    private Object paramValue;

    public void fillParamValue(InState inState) {
        if (refDefinition.getCurrentTable()) {
            this.paramValue = inState.findTableColumnValue(refDefinition.getRefColumnName());
        } else {
            if (CollectionUtils.isEmpty(refDefinition.getRefConditions())) {
                this.paramValue = inState.findTableColumnValue(refDefinition.getRefNo(), refDefinition.getRefTableName(), refDefinition.getRefColumnName());
            } else {
                this.paramValue = inState.findTableColumnValue(refDefinition.getRefNo(), refDefinition.getRefTableName(), refDefinition.getRefColumnName(), refDefinition.getRefConditions());
            }
        }
    }
}
