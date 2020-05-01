package nny.build.data.builder.model.rule;

import nny.build.data.builder.exception.ValueComputeException;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.model.build.ReferenceDefinition;
import nny.build.data.builder.model.table.TableColumn;
import nny.build.data.builder.model.table.TableInfo;
import nny.build.data.builder.service.IRuleCompute;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 地区
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Slf4j
@Getter
@Setter
public class DistrictValueRule extends ValueRule implements IRuleCompute, Serializable {
    private static final long serialVersionUID = 8364294457745199482L;

    /**
     * 引用对象定义
     */
    private ReferenceDefinition refDefinition;

    /**
     * 引用字段表达式
     */
    private String refColumnExpression;

    @Override
    public Object compute(InState inState) {

        TableInfo tableInfo = inState.getTableInfo();
        TableColumn tableColumn = inState.getTableColumn();

        try {
            if (this.buildExpressionObject.getExpressionBoolResult()) {
                this.refDefinition = BuildExpression.parseRefColumnExpression(inState, this.refColumnExpression);
                return getDistrict(inState);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ValueComputeException(String.format(
                    "字段生成异常,类型:{%s} tableNo:{%s},tableName:{%s},columnName:{%s} expression:'%s'",
                    this.type, tableInfo.getNo(), tableInfo.getTableName(), tableColumn.getColumnName(),
                    this.refColumnExpression));
        }
        return super.compute(inState);

    }

    private String getDistrict(InState inState) {
        String idCard = null;
        if (refDefinition.getCurrentTable()) {
            idCard = inState.findTableColumnValue(refDefinition.getRefColumnName()).toString();
        } else {
            idCard = inState.findTableColumnValue(refDefinition.getRefNo(), refDefinition.getRefTableName(), refDefinition.getRefColumnName()).toString();
        }
        //取得身份证号，从身份证号中获取区县,身份证前6位
        return idCard.substring(0, 6);
    }
}
