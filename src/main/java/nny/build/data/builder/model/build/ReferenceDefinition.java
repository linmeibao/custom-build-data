package nny.build.data.builder.model.build;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 引用对象定义
 *
 * @author shengyong.huang
 * @date 2020-04-23
 */
@Getter
@Setter
public class ReferenceDefinition implements Serializable {

    private static final long serialVersionUID = 6815232563968527146L;

    public ReferenceDefinition() {
    }

    public ReferenceDefinition(Boolean currentTable, Integer refNo, String refTableName, String refIdCardColumnName) {
        this.currentTable = currentTable;
        this.refNo = refNo;
        this.refTableName = refTableName;
        this.refColumnName = refIdCardColumnName;
    }

    public ReferenceDefinition(Boolean currentTable, Integer refNo, String refTableName, String refColumnName, List<BuildCondition> refConditions) {
        this.currentTable = currentTable;
        this.refNo = refNo;
        this.refTableName = refTableName;
        this.refColumnName = refColumnName;
        this.refConditions = refConditions;
    }

    /**
     * 引用列是否是当前表
     */
    private Boolean currentTable;
    /**
     * 表序号
     */
    private Integer refNo;
    /**
     * 引用表名
     */
    private String refTableName;
    /**
     * 引用列名
     */
    private String refColumnName;
    /**
     * 条件集合
     */
    private List<BuildCondition> refConditions;


}
