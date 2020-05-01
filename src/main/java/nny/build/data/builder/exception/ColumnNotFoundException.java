package nny.build.data.builder.exception;

/**
 * 字段未找到异常
 *
 * @author shengyong.huang
 * @date 2020-04-25
 */
public class ColumnNotFoundException extends RuntimeException {


    private static final long serialVersionUID = -894704032560929745L;

    public ColumnNotFoundException() {
        super();
    }

    public ColumnNotFoundException(String message) {
        super(message);
    }
}
