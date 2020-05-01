package nny.build.data.builder.exception;

/**
 * 规则计算异常
 *
 * @author shengyong.huang
 * @date 2020-04-25
 */
public class ValueComputeException extends RuntimeException {


    private static final long serialVersionUID = -894704032560929745L;

    public ValueComputeException() {
        super();
    }

    public ValueComputeException(String message) {
        super(message);
    }
}
