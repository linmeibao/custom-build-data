package nny.build.data.builder.service;

import nny.build.data.builder.model.InState;

/**
 * 构建数据接口
 *
 * @author shengyong.huang
 * @date 2020-04-19
 */
public interface IBuildCompute {

    /**
     * 初始化
     *
     * @param inState 中间状态
     */
    void initialize(InState inState);

    /**
     * 构建数据
     *
     * @param inState 中间状态
     */
    void build(InState inState);
}
