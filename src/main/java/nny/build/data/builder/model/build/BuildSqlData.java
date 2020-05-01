package nny.build.data.builder.model.build;

import nny.build.data.builder.enums.OperateMode;
import nny.build.data.builder.model.table.TableColumn;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 构建预编译Sql数据对象
 *
 * @author shengyong.huang
 * @date 2020-02-25
 */
@Getter
@Setter
public class BuildSqlData {

    /**
     * 操作模式
     */
    private OperateMode operateMode;

    /**
     * 预编译SQL
     */
    private String sqlStatement;

    /**
     * sql数据
     */
    private List<List<TableColumn>> columnDataList;

}
