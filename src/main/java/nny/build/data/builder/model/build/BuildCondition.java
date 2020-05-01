package nny.build.data.builder.model.build;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 引用条件值
 *
 * @author shengyong.huang
 * @date 2020-04-18
 */
@Getter
@Setter
public class BuildCondition implements Serializable {
    private static final long serialVersionUID = 2289261571167203454L;

    public BuildCondition() {
    }

    public BuildCondition(String conditionColumn, Object conditionColumnValue) {
        this.conditionColumn = conditionColumn;
        this.conditionColumnValue = conditionColumnValue;
    }

    /**
     * 引用列名称
     */
    private String conditionColumn;

    /**
     * 引用列的条件值
     */
    private Object conditionColumnValue;
}
