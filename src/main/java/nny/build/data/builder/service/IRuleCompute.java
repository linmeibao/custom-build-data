package nny.build.data.builder.service;

import nny.build.data.builder.model.InState;

/**
 * 字段值规则计算接口
 *
 * @author shengyong.huang
 * @date 2020-02-19
 */
public interface IRuleCompute {

    /**
     * 字段值规则计算
     * @param inState 中间状态数据
     * @return 结果值
     */
    Object compute(InState inState);
}
