package com.hebtu.havefun.entity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Proxy;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/23 15:48
 * @projectName HaveFun
 * @className Pictures.java
 * @description TODO
 */
@Entity
@Component
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Table(name = "picture")
@Proxy(lazy = false)
public class Picture implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //数据库表自增id
    private Integer pictureId;
    @OneToOne
    @JoinColumn(name = "activity_id")
    //一对一关联activity对象
    private Activity activity;
    @ManyToOne
    @JoinColumn(name = "activity_detail_id")
    //多对一关联活动详情对象
    private ActivityDetail activityDetail;
    @Column(name = "picture_name")
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
