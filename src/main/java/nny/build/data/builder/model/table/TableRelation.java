package nny.build.data.builder.model.table;

import nny.build.data.builder.enums.ObjectRelationType;
import nny.build.data.builder.exception.BuilderException;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.build.BuildExpression;
import nny.build.data.builder.service.IBuildCompute;
import nny.build.data.builder.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 表关联对象
 *
 * @author shengyong.huang
 * @date 2020-02-20
 */

@Slf4j
@Getter
@Setter
public class TableRelation implements IBuildCompute, Serializable {

    private static final long serialVersionUID = -763917761414171296L;

    /**
     * 关联方式
     */
    private ObjectRelationType relationType;

    /**
     * 关联表数据生成条数以及行号
     */
    private Integer relationTableRowNum;

    /**
     * 关联表数据生成条数所依赖的表达式对象
     */
    private BuildExpression relationRowNumExpressionObject;

    /**
     * 关联表数据生成条数所依赖的表达式
     */
    private String relationRowNumExpression;

    /**
     * 关联表
     */
    private TableInfo relationTable;

    /**
     * 关联表生成的结果数据
     */
    private List<List<TableColumn>> relationColumns = new ArrayList<>();

    /**
     * 描述
     */
    private String desc;

    @Override
    public void initialize(InState inState) {

        // 解析表达式填充关联引用对象
        if (StringUtils.isNotEmpty(relationRowNumExpression)) {
            this.relationRowNumExpressionObject = BuildExpression.parseExpression(inState, relationRowNumExpression);
            this.relationRowNumExpressionObject.expressionEvaluation(inState);
            try {
                this.relationTableRowNum = Integer.valueOf(relationRowNumExpressionObject.getExpressionResult().toString());
            } catch (Exception e) {
                e.printStackTrace();
                throw new BuilderException(String.format("relationRowNumExpression计算异常 relationRowNumExpression {%s} TableInfo {%s} ", relationRowNumExpression, relationTable.getTableName()));
            }
        }

        if (relationTableRowNum == null) {
            throw new BuilderException(String.format("relationTableRowNum不能为空 relationTableRowNum和relationRowNumExpression必须指定一个 TableInfo {%s} ", relationTable.getTableName()));
        }

        // 重置连续值
        inState.setIncrementValue(0);

        // 初始化表
        relationTable.initialize(inState);

    }

    @Override
    public void build(InState inState) {

        List<TableColumn> baseTableColumns = CommonUtils.deepCopy(relationTable.getColumns());

        for (Integer i = 0; i < this.relationTableRowNum; i++) {

            // 设置当前计算到第几条数据
            inState.setTableDataRowNum(i);

            relationTable.build(inState);

            List<TableColumn> tempList = CommonUtils.deepCopy(relationTable.getColumns());

            if (CollectionUtils.isNotEmpty(relationTable.getWheres())) {
//                for (TableColumn where : relationTable.getWheres()) {
//                    relationColumns.get(i).add(where);
//                }
                tempList.addAll(CommonUtils.deepCopy(relationTable.getWheres()));
            }
            relationColumns.add(tempList);


            if (relationTableRowNum > 1) {
                relationTable.setColumns(CommonUtils.deepCopy(baseTableColumns));
            }
        }

        inState.setTableDataRowNum(null);
    }
}
