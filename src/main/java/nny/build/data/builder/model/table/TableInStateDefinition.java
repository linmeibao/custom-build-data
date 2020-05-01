package nny.build.data.builder.model.table;

import nny.build.data.builder.model.InState;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 表计算中间状态变量定义
 *
 * @author shengyong.huang
 * @date 2020-04-17
 */
@Getter
@Setter
public class TableInStateDefinition implements Serializable {

    private static final long serialVersionUID = -5897385016135751783L;

    /**
     * 字段数据固定值
     * "txa_type": [
     * "08",
     * "09"
     * ],
     * "txn_code": [
     * "20000",
     * "20150"
     * ],
     */
    private Map<String, Object[]> tableFixedValues;

    /**
     * 字段数据固定值的替代值
     * "placeholder_txa_type": "09",
     * "placeholder_txn_code": "20050",
     * "placeholder_loan_billing_tenor": "CONTINUOUS_VALUE",
     */
    private Map<String, Object> fixedPlaceholder;

    /**
     * 字段数据动态值
     * TXN_EFFECTIVE_AMNT:TableColumn
     */
    private Map<String, List<TableColumn>> tableDynamicValues;

    /**
     * 字段数据动态值的替代值
     * placeholder_TXN_EFFECTIVE_AMNT:TableColumn
     */
    private Map<String, TableColumn> dynamicPlaceholder;

    public void fill(InState inState){

        if (MapUtils.isNotEmpty(tableDynamicValues)){
            for (Map.Entry<String, List<TableColumn>> entry : tableDynamicValues.entrySet()) {
                for (TableColumn tableColumn : entry.getValue()) {
                    tableColumn.initialize(inState);
                    tableColumn.build(inState);
                }
            }
        }

        if (MapUtils.isNotEmpty(dynamicPlaceholder)){
            for (Map.Entry<String, TableColumn> entry : dynamicPlaceholder.entrySet()) {
                TableColumn tableColumn = entry.getValue();
                tableColumn.initialize(inState);
                tableColumn.build(inState);
            }
        }

        inState.setTableInStateDefinition(this);
    }

}
