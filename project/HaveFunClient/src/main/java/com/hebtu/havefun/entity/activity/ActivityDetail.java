package com.hebtu.havefun.entity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Proxy;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/23 16:33
 * @projectName HaveFun
 * @className ActivityDetail.java
 * @description TODO
 */
@Component
@Entity
@Table(name = "activity_detail")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
public class ActivityDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //数据库表自增id
    private Integer activityDetailId;
    @OneToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;
    //活动详细信息
    @Column(name = "activity_info")
    private String activityInfo;
    //其他信息
    @Column(name = "other_info")
    private String otherInfo;
    //活动图片
    @OneToMany(mappedBy = "activityDetail",cascade = CascadeType.ALL)
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
