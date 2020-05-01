package nny.build.data.builder.exception;

/**
 * 数据源异常
 *
 * @author shengyong.huang
 * @date 2020-04-25
 */
public class DataSourceException extends RuntimeException {


    private static final long serialVersionUID = -894704032560929745L;

    public DataSourceException() {
        super();
    }

    public DataSourceException(String message) {
        super(message);
    }
}
