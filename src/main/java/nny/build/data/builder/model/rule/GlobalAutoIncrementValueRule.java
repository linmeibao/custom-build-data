package nny.build.data.builder.model.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.config.BuilderConfig;
import nny.build.data.builder.config.BuilderConfigLoader;
import nny.build.data.builder.exception.BuilderException;
import nny.build.data.builder.model.InState;
import nny.build.data.builder.model.rule.ValueRule;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局自增Id
 *
 * @author shengyong.huang
 * @date 2020-02-24
 */
@Slf4j
@Getter
@Setter
public class GlobalAutoIncrementValueRule extends ValueRule implements Serializable {

    private static final long serialVersionUID = 6204217031539368861L;
    /**
     * 全局自增Id
     */
    private static AtomicInteger globalAutoIncrement = null;
    /**
     * 配置文件
     */
    private static BuilderConfig builderConfig = null;

    static {
        builderConfig = BuilderConfigLoader.getBuilderConfig();
        try {
            String str = FileUtils.readFileToString(new File(builderConfig.getGlobalAutoIncrementFilePath()), "UTF-8");
            globalAutoIncrement = new AtomicInteger(Integer.valueOf(str));
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BuilderException("全局自增Id文件读取失败");
        }
    }

    @Override
    public Object getRuleValue(InState inState) {
        return globalAutoIncrement.getAndIncrement();
    }

    public static void writeBackGlobalAutoIncrement() {
        try {
            FileUtils.writeStringToFile(new File(builderConfig.getGlobalAutoIncrementFilePath()), globalAutoIncrement.toString(), "UTF-8");
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BuilderException("全局自增Id文件回写失败");
        }
        log.info("全局自增Id文件回写成功,当前Id值 {}", globalAutoIncrement.get());
    }
}
