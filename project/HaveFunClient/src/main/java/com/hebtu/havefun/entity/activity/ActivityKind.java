package com.hebtu.havefun.entity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Proxy;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/29 16:22
 * @projectName HaveFun
 * @className ActivityKind.java
 * @description TODO
 */
@Entity
@Table(name = "activity_kind")
@Component
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
public class ActivityKind implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    //数据库表自增id
    private Integer Id;
    @Column(name = "name")
    //大类名称
    private String kindName;
    @OneToMany(mappedBy = "activityKind", cascade = CascadeType.ALL)
    //一对一小类对象
    private Set<TypeOfKind> typeOfKind;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public Set<TypeOfKind> getTypeOfKind() {
        return typeOfKind;
    }

    public void setTypeOfKind(Set<TypeOfKind> typeOfKind) {
        this.typeOfKind = typeOfKind;
    }

    @Override
    public String toString() {
        return "ActivityKind{" +
                "Id=" + Id +
                ", kindName='" + kindName + '\'' +
                ", typeOfKind=" + typeOfKind +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
