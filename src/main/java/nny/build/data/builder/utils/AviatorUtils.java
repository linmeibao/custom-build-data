package nny.build.data.builder.utils;

import com.alibaba.fastjson.JSONObject;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import nny.build.data.builder.exception.BuilderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * 表达式计算工具
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Slf4j
public class AviatorUtils {

    static {
        //注册函数
        AviatorEvaluator.addFunction(new DateParseFunction());
        AviatorEvaluator.addFunction(new DateParseTimeFunction());
        try {
            AviatorEvaluator.addStaticFunctions("dateUtils", DateUtils.class);
            AviatorEvaluator.addStaticFunctions("dateFormatUtils", DateFormatUtils.class);
        } catch (Exception e) {
            log.error("表达式计算工具,注册函数失败 {}", e.getMessage());
            throw new BuilderException(String.format("表达式计算工具,注册函数失败 {%s}", e.getMessage()));
        }

    }

    /**
     * 计算bool结果
     *
     * @param expression
     * @param env
     * @return
     */
    public static Boolean boolExpressionExecute(String expression, Map<String, Object> env) {
        log.debug("bool表达式计算 {} {}", expression, JSONObject.toJSONString(env));
        Expression compiledExp = AviatorEvaluator.compile(expression);
        Object v = compiledExp.execute(env);
        return (Boolean) v;
    }

    /**
     * 表达式计算
     *
     * @param expression
     * @param env
     * @return
     */
    public static Object expressionExecute(String expression, Map<String, Object> env) {
        log.debug("表达式计算 {} {}", expression, JSONObject.toJSONString(env));
        Expression compiledExp = AviatorEvaluator.compile(expression);
        return compiledExp.execute(env);
    }

    /**
     * 字符串转日期自定义函数
     */
    private static class DateParseFunction extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject date, AviatorObject format) {

            String dateStr = FunctionUtils.getStringValue(date, env);
            String formatStr = FunctionUtils.getStringValue(format, env);

            Date d = null;
            try {
                d = DateUtils.parseDate(dateStr, new String[]{formatStr});
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return AviatorRuntimeJavaType.valueOf(d);
        }

        @Override
        public String getName() {
            return "parseDateFn";
        }
    }

    /**
     * 字符串转日期时间戳LONG自定义函数
     */
    private static class DateParseTimeFunction extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject date, AviatorObject format) {

            String dateStr = FunctionUtils.getStringValue(date, env);
            String formatStr = FunctionUtils.getStringValue(format, env);

            Long time = null;
            try {
                time = DateUtils.parseDate(dateStr, new String[]{formatStr}).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return AviatorRuntimeJavaType.valueOf(time);
        }

        @Override
        public String getName() {
            return "parseDateGetTimeFn";
        }
    }

    public static void main(String[] args) {
        System.out.println(AviatorEvaluator.execute("12 + 1"));
    }

}
