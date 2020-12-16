package com.hyphenate.easeui.activity;
import java.io.Serializable;

public class TypeOfKind implements Serializable {
    //数据库表自增id
    private Integer id;
    //小类名称
    private String typeName;
    //一对一关联对象
    private ActivityKind activityKind;
    private Activity activity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public ActivityKind getActivityKind() {
        return activityKind;
    }

    public void setActivityKind(ActivityKind activityKind) {
        this.activityKind = activityKind;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
