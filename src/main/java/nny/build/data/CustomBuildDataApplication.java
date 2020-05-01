package nny.build.data;

import nny.build.data.builder.runner.ApplicationRunner;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据发送主类
 *
 * @author shengyong.huang

 * @date 2019/8/26
 */
@Slf4j
public class CustomBuildDataApplication {

    public static void main(String[] args) {
        log.info("自动化测试启动, args:{}", args);
        ApplicationRunner runner = new ApplicationRunner();
        runner.initialize(args);
        runner.start();
    }
}
