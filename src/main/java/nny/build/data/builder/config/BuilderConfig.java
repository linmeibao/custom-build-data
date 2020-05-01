package nny.build.data.builder.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置文件
 *
 * @author shengyong.huang
 * @date 2019/8/26
 */
@Slf4j
@Getter
@Setter
public class BuilderConfig {

    /**
     * 多线程配置
     */
    private ThreadConfig threadConfig;

    /**
     * 数据库配置
     */
    private DataSourceConfig dataSourceConfig;

    /**
     * 序列化示例文件配置
     */
    private SerializeExampleConfig exampleConfig;

    /**
     * 设置构建多少组数据
     */
    private Integer buildNumber;

    /**
     * 测试数据json文件路径
     */
    private String dataJsonFilePath;

    /**
     * 测试数据DDL配置json文件
     */
    private String dataDDLJsonFilePath;

    /**
     * 自动生成的sql语句输出路径
     */
    private String sqlFileOutputFilePath;

    /**
     * 全局自增Id保存文件地址
     */
    private String globalAutoIncrementFilePath;
}
