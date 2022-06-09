package nny.build.data.builder.model.build;

import nny.build.data.builder.exception.BuilderException;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.utils.AviatorUtils;
import nny.build.data.builder.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 构建表达式对象
 *
 * @author shengyong.huang
 * @date 2020-04-17
 */
@Getter
@Setter
public class BuildExpression implements Serializable {

    private static final long serialVersionUID = 7856372804903789578L;


    public BuildExpression() {
    }

    public BuildExpression(boolean expressionBoolResult) {
        this.expressionBoolResult = expressionBoolResult;
    }

    public BuildExpression(String expression, List<BuildExpressionParam> buildExpressionParams) {
        this.expression = expression;
        this.buildExpressionParams = buildExpressionParams;
    }

    /**
     * 表达式
     */
    private String expression;

    /**
     * 表达式参数
     */
    private List<BuildExpressionParam> buildExpressionParams;

    /**
     * 表达式bool结果
     */
    private Boolean expressionBoolResult;

    /**
     * 表达式结果
     */
    private Object expressionResult;

    /**
     * 计算bool表达式结果
     * 进行填充参数对象的值&直接计算
     *
     * @param inState 中间状态
     */
    public void boolExpressionEvaluation(InState inState) {
        Map<String, Object> paramMap = new HashMap<>(10);
        buildExpressionParams.forEach(p -> p.fillParamValue(inState));
        buildExpressionParams.forEach(p -> paramMap.put(p.getRefDefinition().getRefColumnName(), p.getParamValue()));
        this.expressionBoolResult = AviatorUtils.boolExpressionExecute(this.expression, paramMap);
    }

    /**
     * 计算bool表达式结果
     * 需要先填充表达式的参数值,才能进行计算,否则会报错
     *
     * @param appendParamMap 追加参数map集合
     */
    public void boolExpressionEvaluation(Map<String, Object> appendParamMap) {
        Map<String, Object> paramMap = new HashMap<>(10);
        buildExpressionParams.forEach(p -> paramMap.put(p.getRefDefinition().getRefColumnName(), p.getParamValue()));
        paramMap.putAll(appendParamMap);
        this.expressionBoolResult = AviatorUtils.boolExpressionExecute(this.expression, paramMap);
    }

    /**
     * 计算bool表达式结果
     * 需要先填充表达式的参数值,才能进行计算,否则会报错
     * 追加单组参数
     *
     * @param paramName  参数名称
     * @param paramValue 参数值
     */
    public void boolExpressionEvaluation(String paramName, Object paramValue) {
        Map<String, Object> paramMap = new HashMap<>(10);
        buildExpressionParams.forEach(p -> paramMap.put(p.getRefDefinition().getRefColumnName(), p.getParamValue()));
        paramMap.put(paramName, paramValue);
        this.expressionBoolResult = AviatorUtils.boolExpressionExecute(this.expression, paramMap);
    }

    /**
     * 计算bool表达式结果
     * 进行填充参数对象的值&直接计算
     *
     * @param inState 中间状态
     */
    public void expressionEvaluation(InState inState) {
        Map<String, Object> paramMap = new HashMap<>(10);
        buildExpressionParams.forEach(p -> p.fillParamValue(inState));
        buildExpressionParams.forEach(p -> paramMap.put(p.getRefDefinition().getRefColumnName(), p.getParamValue()));
        try {
            this.expressionResult = AviatorUtils.expressionExecute(this.expression, paramMap);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 解析引用表达式
     *
     * @param inState    中间状态
     * @param expression 引用表达式
     * @return 表达式对象
     */
    public static BuildExpression parseExpression(InState inState, String expression) {
        List<String> refValueList = CommonUtils.matchRefExpression(expression);
        List<BuildExpressionParam> expressionParams = new ArrayList<>(8);

        // 填充布尔表达式参数
        for (String refValue : refValueList) {
            ReferenceDefinition referenceDefinition = parseRefColumnExpression(inState, refValue);
            String[] values = refValue.split("\\.");
            String refExpName = StringUtils.join(values, ".");
            expression = expression.replace(refExpName, values[2]);
            expressionParams.add(new BuildExpressionParam(referenceDefinition));

        }
        // 替换表达式中的${xxx.xx.xx} 为 字段名称
        expression = expression.replace("${", "").replace("}", "");
        return new BuildExpression(expression, expressionParams);
    }

    /**
     * 解析引用字段表达式
     *
     * @param inState             中间状态
     * @param refColumnExpression 引用字段表达式
     * @return 表达式对象
     */
    public static ReferenceDefinition parseRefColumnExpression(InState inState, String refColumnExpression) {

        refColumnExpression = refColumnExpression.replace("${", "").replace("}", "");

        String[] refColumns = refColumnExpression.split("\\.");

        if (refColumns.length < 3) {
            throw new BuilderException(String.format("引用字段表达式配置错误 {%s}", refColumnExpression));
        }

        boolean currentTable = false;
        Integer tableNo = null;
        String tableName = null;

        // 判断是否为当前表
        if (refColumns[0].equals(inState.getTableInfo().getNo().toString()) && refColumns[1].equals(inState.getTableInfo().getTableName())) {
            currentTable = true;
        } else {
            tableNo = Integer.valueOf(refColumns[0]);
            tableName = refColumns[1];
        }

        if (refColumns.length > 3) {
            List<String> paramList = CommonUtils.matchRefParamExpression(refColumns[3]);
            List<BuildCondition> conditions = new ArrayList<>(10);

            for (String param : paramList) {
                String[] params = param.split(":");
                conditions.add(new BuildCondition(params[0], params[1]));
            }

            return new ReferenceDefinition(currentTable, tableNo, tableName, refColumns[2], conditions);
        }

        return new ReferenceDefinition(currentTable, tableNo, tableName, refColumns[2]);
    }
}
