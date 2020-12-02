package com.hebtu.havefun.entity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Proxy;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/29 16:17
 * @projectName HaveFun
 * @className ActivityType.java
 * @description TODO
 */
@Entity
@Table(name = "type_of_kind")
@Component
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
public class TypeOfKind implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //数据库表自增id
    private Integer id;
    @Column(name = "name")
    //小类名称
    private String typeName;
    @ManyToOne
    //一对一关联对象
    private ActivityKind activityKind;
    @OneToOne
    @JoinColumn(name = "activity_id")
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
        return "TypeOfKind{" +
                "id=" + id +
                ", typeName='" + typeName + '\'' +
                ", activityKind=" + activityKind +
                ", activity=" + activity +
                '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
