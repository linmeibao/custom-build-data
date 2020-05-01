package nny.build.data.builder.enums;

/**
 * 对象关联关系
 * @author shengyong.huang
 * @date 2018/11/30
 */
public enum ObjectRelationType {
    ONE_TO_NNE("一对一"),
    ONE_TO_MANY("一对多"),
    MANY_TO_ONE("多对一");

    private String desc;

    ObjectRelationType(String desc){
        this.desc = desc;
    }
    public String getDesc() {
        return desc;
    }
}
