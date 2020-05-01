package nny.build.data.builder.model.table;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 表索引
 *
 * @author shengyong.huang
 * @date 2020-02-20
 */
@Getter
@Setter
public class TableIndex implements Serializable {

    private static final long serialVersionUID = -2296878640063222189L;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 列名称
     */
    private String columnName;
}
