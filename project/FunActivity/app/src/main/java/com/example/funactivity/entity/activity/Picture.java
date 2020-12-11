package com.example.funactivity.entity.activity;

import java.io.Serializable;

public class Picture implements Serializable {
    //数据库表自增id
    private Integer pictureId;
    //一对一关联activity对象
    private Activity activity;
    //多对一关联活动详情对象
    private ActivityDetail activityDetail;
    //图片路径
    private String pictureName;

    public Integer getPictureId() {
        return pictureId;
    }

    public void setPictureId(Integer pictureId) {
        this.pictureId = pictureId;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ActivityDetail getActivityDetail() {
        return activityDetail;
    }

    public void setActivityDetail(ActivityDetail activityDetail) {
        this.activityDetail = activityDetail;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }
}
