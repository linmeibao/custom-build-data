package nny.build.data.builder.exception;

/**
 * 构建数据异常
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
public class BuilderException extends RuntimeException {

    private static final long serialVersionUID = -6458995064402326229L;

    public BuilderException() {
        super();
    }

    public BuilderException(String message) {
        super(message);
    }
}
