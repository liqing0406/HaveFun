package com.example.funactivity.entity.activity;


import java.io.Serializable;
import java.util.Set;

public class ActivityDetail implements Serializable {
    //数据库表自增id
    private Integer activityDetailId;
    //一对一关联活动对象
    private Activity activity;
    //活动详细信息
    private String activityInfo;
    //其他信息
    private String otherInfo;
    //活动图片
    private Set<Picture> activityPictures;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }


    public String getActivityInfo() {
        return activityInfo;
    }

    public void setActivityInfo(String activityInfo) {
        this.activityInfo = activityInfo;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public Integer getActivityDetailId() {
        return activityDetailId;
    }

    public void setActivityDetailId(Integer activityDetailId) {
        this.activityDetailId = activityDetailId;
    }

    public Set<Picture> getActivityPictures() {
        return activityPictures;
    }

    public void setActivityPictures(Set<Picture> activityPictures) {
        this.activityPictures = activityPictures;
    }
    @Override
    public String toString() {
        return "ActivityDetail{" +
                "activityDetailId=" + activityDetailId +
                ", activity=" + activity +
                ", activityInfo='" + activityInfo + '\'' +
                ", otherInfo='" + otherInfo + '\'' +
                ", activityPictures=" + activityPictures +
                '}';
    }
}
