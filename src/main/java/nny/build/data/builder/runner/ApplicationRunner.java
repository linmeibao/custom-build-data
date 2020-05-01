package nny.build.data.builder.runner;

import com.alibaba.fastjson.JSONObject;
import nny.build.data.builder.config.BuilderConfig;
import nny.build.data.builder.config.BuilderConfigLoader;
import nny.build.data.builder.config.DataSourceConfig;
import nny.build.data.builder.config.ThreadConfig;
import nny.build.data.builder.exception.BuilderException;
import nny.build.data.builder.model.BusinessDataJson;
import nny.build.data.builder.model.build.BuildData;
import nny.build.data.builder.model.rule.GlobalAutoIncrementValueRule;
import nny.build.data.builder.model.table.TableInfo;
import nny.build.data.builder.service.BuilderService;
import nny.build.data.builder.service.IJdbcService;
import nny.build.data.builder.service.JdbcServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 应用运行类
 *
 * @author shengyong.huang
 * @date 2019/8/26
 */
@Slf4j
public class ApplicationRunner {

    private BuilderService builderService;

    private IJdbcService jdbcService;

    /**
     * 基于java命令行传入参数执行
     *
     * @param args 配置文件地址
     */
    public void initialize(String[] args) {
        if (args != null && args.length >= 1) {

            BuilderConfigLoader.initialize(args[0]);
            log.info("配置文件加载完成");

            BuilderConfig builderConfig = BuilderConfigLoader.getBuilderConfig();

            // 初始化数据库连接
            builderConfig.getDataSourceConfig().fillConnections();
            // 生成示例文件
            builderConfig.getExampleConfig().generate();

            jdbcService = new JdbcServiceImpl();

            builderService = new BuilderService(jdbcService);
        } else {
            log.error("第一个参数需要为配置文件的路径,{}", Arrays.toString(args));
            throw new BuilderException(String.format("参数文件路径未配置:%s", Arrays.toString(args)));
        }
    }

    /**
     * 调用此方法开始生成数据
     */
    public void start() {
        BuilderConfig builderConfig = BuilderConfigLoader.getBuilderConfig();

        try {
            if (!BuilderConfigLoader.isLoad()) {
                throw new BuilderException("配置未加载,请先调用初始化方法加载配置文件");
            }

            ThreadConfig threadConfig = builderConfig.getThreadConfig();

            // 配置文件加载 业务数据生成JSON
            final BusinessDataJson businessDataJson = builderService.loadJsonFile(builderConfig.getDataJsonFilePath());

            // 构建DDL
            buildDdl(builderConfig);

            // 清理Sql日志文件夹
            cleanSqlNoteFolder(builderConfig);

            // 构建数据
            if (threadConfig.getSingleThreadDebug()) {
                singleThread(builderConfig, businessDataJson);
            } else {
                multiThread(builderConfig, businessDataJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.cleanResources(builderConfig);
        }


    }

    /**
     * 构建DDL
     *
     * @param builderConfig 配置文件
     */
    private void buildDdl(BuilderConfig builderConfig) {

        DataSourceConfig dataSourceConfig = builderConfig.getDataSourceConfig();

        if (dataSourceConfig.getAutoDDl()) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("DDL任务");

            // 配置文件加载 业务数据生成JSON
            final List<TableInfo> tableInfos = builderService.loadDDlJsonFile(builderConfig.getDataDDLJsonFilePath());

            // dbKey-sqlList
            Map<String, List<String>> sqlMap = builderService.buildDDl(tableInfos);

            // 执行DDL
            boolean result = jdbcService.executeDdl(dataSourceConfig, sqlMap);

            if (!result) {
                throw new BuilderException(String.format("DDL执行失败, {%s}", sqlMap));
            }

            log.info("DDL执行成功: {}", JSONObject.toJSONString(sqlMap));

            stopWatch.stop();
            log.info(stopWatch.prettyPrint());
        }
    }

    /**
     * 线程池执行
     *
     * @param builderConfig    配置文件
     * @param businessDataJson 数据配置JSON
     */
    private void multiThread(BuilderConfig builderConfig, BusinessDataJson businessDataJson) {
        StopWatch stopWatch = new StopWatch();
        ThreadConfig threadConfig = builderConfig.getThreadConfig();


        int builderNumber = builderConfig.getBuildNumber();
        int batchSize = threadConfig.getBatchSize();

        final int count;
        int lastSize;

        if (builderNumber % batchSize == 0) {
            count = builderNumber / batchSize;
            lastSize = batchSize;
        } else {
            count = builderNumber / batchSize + 1;
            lastSize = builderNumber % batchSize;
        }

        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(threadConfig.getThreadBuilderNumber());

        CountDownLatch countDownLatch = new CountDownLatch(count);

        stopWatch.start("批量任务");

        for (int i = 0; i < count; i++) {

            if (i == count - 1) {
                batchSize = lastSize;
            }

            final int size = batchSize;

            newFixedThreadPool.execute(() -> {

                log.info("{} 开始生成业务数据 ", Thread.currentThread().getName());

                List<BuildData> buildDataList = builderService.build(businessDataJson, size);
                builderService.storage(buildDataList, builderConfig);

                countDownLatch.countDown();
                log.info("{} 成功生成 {} 组数据入库", Thread.currentThread().getName(), buildDataList.size());
                log.info("已生成 {} 组数据入库", (count - countDownLatch.getCount() - 1) * size + lastSize);

            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopWatch.stop();


        log.info(stopWatch.prettyPrint());

        newFixedThreadPool.shutdown();
    }

    /**
     * 单线程调试
     *
     * @param builderConfig    配置文件
     * @param businessDataJson 数据配置JSON
     */
    private void singleThread(BuilderConfig builderConfig, BusinessDataJson businessDataJson) {

        StopWatch stopWatch = new StopWatch();
        ThreadConfig threadConfig = builderConfig.getThreadConfig();

        int builderNumber = builderConfig.getBuildNumber();
        int batchSize = threadConfig.getBatchSize();
        int tempBatchSize = batchSize;

        int count;
        int lastSize;

        if (builderNumber % batchSize == 0) {
            count = builderNumber / batchSize;
            lastSize = batchSize;
        } else {
            count = builderNumber / batchSize + 1;
            lastSize = builderNumber % batchSize;
        }


        for (int i = 0; i < count; i++) {
            log.info("【开始】生成业务数据...");

            if (i == count - 1) {
                batchSize = lastSize;
            }

            stopWatch.start("数据生成任务" + i);

            List<BuildData> buildDataList = builderService.build(businessDataJson, batchSize);

            if (count != 1 && i == count - 1) {
                log.info("【结束】生成 {} 组... ", tempBatchSize * i + lastSize);
            } else {
                log.info("【结束】生成 {} 组... ", buildDataList.size() * (i + 1));
            }
            stopWatch.stop();


            stopWatch.start("数据持久化" + i);
            builderService.storage(buildDataList, builderConfig);
            stopWatch.stop();


            log.info(stopWatch.prettyPrint());
        }

    }


    /**
     * 释放资源
     *
     * @param builderConfig 配置文件
     */
    private void cleanResources(BuilderConfig builderConfig) {
        DataSourceConfig dataSourceConfig = builderConfig.getDataSourceConfig();
        jdbcService.closeDbConnection(dataSourceConfig);
        GlobalAutoIncrementValueRule.writeBackGlobalAutoIncrement();

        String todayStr = DateFormatUtils.format(new Date(), "yyyyMMdd");
        builderService.mergeSqlNote(builderConfig.getSqlFileOutputFilePath() + todayStr);
    }

    /**
     * 清理Sql日志文件夹
     *
     * @param builderConfig 配置文件
     */
    private void cleanSqlNoteFolder(BuilderConfig builderConfig) {
        String folderPath = builderService.getSqlNoteFolderPath(builderConfig);
        File folder = new File(folderPath);

        if (folder.exists()) {

            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }

            log.info("sql日志文件夹已存在，删除文件夹下所有子文件");
        } else {
            boolean mkdir = folder.mkdir();
            if (mkdir) {
                log.info("sql日志文件夹创建完成");
            } else {
                throw new BuilderException("sql日志文件输出路径文件夹创建失败");
            }
        }
    }


}
