package com.hyphenate.easeui.activity;
import java.io.Serializable;
import java.util.Set;

public class ActivityKind implements Serializable {
    //数据库表自增id
    private Integer Id;
    //大类名称
    private String kindName;
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
}
