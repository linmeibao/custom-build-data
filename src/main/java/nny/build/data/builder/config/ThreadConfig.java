package nny.build.data.builder.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 多线程配置
 *
 * @author shengyong.huang
 * @date 2020-02-23
 */
@Setter
@Getter
public class ThreadConfig {

    /**
     * 线程数
     */
    private Integer threadBuilderNumber;

    /**
     * 批次数量
     */
    private Integer batchSize;

    /**
     * 单线程调试
     */
    private Boolean singleThreadDebug;
}
