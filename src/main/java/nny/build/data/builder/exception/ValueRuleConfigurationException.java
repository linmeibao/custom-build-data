package nny.build.data.builder.exception;

/**
 * 规则配置异常
 *
 * @author shengyong.huang
 * @date 2020-04-25
 */
public class ValueRuleConfigurationException extends RuntimeException {


    private static final long serialVersionUID = -894704032560929745L;

    public ValueRuleConfigurationException() {
        super();
    }

    public ValueRuleConfigurationException(String message) {
        super(message);
    }
}
