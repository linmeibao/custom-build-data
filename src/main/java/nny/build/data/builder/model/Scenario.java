package nny.build.data.builder.model;


import nny.build.data.builder.model.table.TableInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 业务场景对象
 *
 * @author shengyong.huang
 * @date 2020-04-17
 */
@Getter
@Setter
public class Scenario implements Serializable {

    private static final long serialVersionUID = -6853767671237117069L;
    /**
     * 表对象
     */
    private List<TableInfo> tableInfos;


    /**
     * 业务场景描述
     */
    private String desc;

}
