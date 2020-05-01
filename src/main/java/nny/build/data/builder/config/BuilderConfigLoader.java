package nny.build.data.builder.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.IOException;

/**
 * 配置加载类
 *
 * @author shengyong.huang
 * @date 2020-02-23
 */
@Slf4j
public class BuilderConfigLoader {

    private static boolean load = false;

    private static BuilderConfig builderConfig = null;

    private BuilderConfigLoader() {

    }

    public static void initialize(String propFile) {
        try {

            log.info("加载配置文件: {}", propFile);
            File file = new File(propFile);
            Object object = Yaml.load(file);

            // jyaml没有对泛型做支持，因此当涉及到较为复杂的反序列化情况时可能会有阻碍
            // List<DataSourceInfo> dataSourceInfos的DataSourceInfo转换成了HashMap
            // 使用json进行反序列化
            String json = JSONObject.toJSONString(object);
            builderConfig = JSONObject.parseObject(json, BuilderConfig.class);
            load = true;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static BuilderConfig getBuilderConfig() {
        return builderConfig;
    }

    public static boolean isLoad() {
        return load;
    }
}
