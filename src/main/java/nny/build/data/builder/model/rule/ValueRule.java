package nny.build.data.builder.model.rule;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nny.build.data.builder.model.InState;

import java.io.Serializable;
import java.util.Map;


/**
 * 字段值数据生成规则类
 *
 * @author shengyong.huang
 * @date 2020-02-18
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, visible = true, property = "type")
@JsonSubTypes({
        // 默认规则
        @JsonSubTypes.Type(value = ValueRule.class, name = "NORMAL"),
        // 随机选择器
        @JsonSubTypes.Type(value = RandomSelectorValueRule.class, name = "SELECTOR"),
        // 随机字符串
        @JsonSubTypes.Type(value = RandomStringValueRule.class, name = "RANDOM_STRING"),
        // 随机金额
        @JsonSubTypes.Type(value = RandomMoneyConditionValueRule.class, name = "RANDOM_MONEY"),
        // 随机数字
        @JsonSubTypes.Type(value = RandomNumberValueRule.class, name = "RANDOM_NUMBER"),
        // 随机日期
        @JsonSubTypes.Type(value = RandomDateConditionValueRule.class, name = "RANDOM_DATE"),
        // 随机手机号
        @JsonSubTypes.Type(value = RandomMobileValueRule.class, name = "RANDOM_MOBILE"),
        // 表内自增值
        @JsonSubTypes.Type(value = ContinuousValueRule.class, name = "CONTINUOUS_VALUE"),
        // 身份证
        @JsonSubTypes.Type(value = IdCardValueRule.class, name = "ID_CARD"),
        // 引用字段
        @JsonSubTypes.Type(value = ReferenceValueRule.class, name = "REFERENCE"),
        // 随机名字
        @JsonSubTypes.Type(value = RandomNameValueRule.class, name = "RANDOM_NAME"),
        // 省份
        @JsonSubTypes.Type(value = ProvinceValueRule.class, name = "PROVINCE"),
        // 城市
        @JsonSubTypes.Type(value = CityValueRule.class, name = "CITY"),
        // 区县
        @JsonSubTypes.Type(value = DistrictValueRule.class, name = "DISTRICT"),
        // 生日
        @JsonSubTypes.Type(value = BirthValueRule.class, name = "BIRTH"),
        // 固定值规则
        @JsonSubTypes.Type(value = FixedValueRule.class, name = "FIXED_VALUE"),
        // 动态值规则
        @JsonSubTypes.Type(value = DynamicValueRule.class, name = "DYNAMIC_VALUE"),
        // 表达式计算
        @JsonSubTypes.Type(value = ExpressionValueRule.class, name = "EXPRESSION"),
        // 全局自增值
        @JsonSubTypes.Type(value = GlobalAutoIncrementValueRule.class, name = "GLOBAL_AUTO_INCREMENT"),
})
@Slf4j
@Getter
@Setter
public class ValueRule extends AbstractValueRule implements Serializable {

    private static final long serialVersionUID = -5627643816464706557L;

    @Override
    public Object getRuleValue(InState inState) {
        return defaultValue;
    }

    @Override
    protected Map<String, Object> errorMessageMap() {
        return null;
    }

}
