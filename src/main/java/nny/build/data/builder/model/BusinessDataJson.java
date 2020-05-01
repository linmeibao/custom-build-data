package nny.build.data.builder.model;

import nny.build.data.builder.exception.BuilderException;
import nny.build.data.builder.model.table.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 业务数据JSON
 *
 * @author shengyong.huang
 * @date 2020-04-17
 */
@Slf4j
public class BusinessDataJson implements Serializable {

    private static final long serialVersionUID = -2241964622693852073L;
    /**
     * 业务场景列表
     */
    private List<Scenario> scenarios;

    /**
     * 使用的业务场景下标
     */
    private Integer scenarioIndex = 0;

    /**
     * 随机选择一个场景
     */
    public void selectScene() {
        this.scenarioIndex = RandomUtils.nextInt(0, scenarios.size());
        if (log.isDebugEnabled()) {
            log.debug("当前选择场景: {}", this.getScenarios().get(this.scenarioIndex).getDesc());
        }
    }


    public List<TableInfo> scenarioTableInfos() {
        if (CollectionUtils.isEmpty(scenarios) || scenarioIndex == null) {
            throw new BuilderException("scenarios不能为空并且scenarioIndex也不能为空");
        }
        return this.scenarios.get(this.scenarioIndex).getTableInfos();
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }
}
