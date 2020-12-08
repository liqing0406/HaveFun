package com.hebtu.havefun.entity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Proxy;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/29 16:20
 * @projectName HaveFun
 * @className ActivityLocation.java
 * @description TODO
 */
@Entity
@Table(name = "activity_location")
@Component
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
public class ActivityLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //数据库表自增id
    private Integer locationId;
    @Column(name = "province")
    //省
    private String Province;
    @Column(name = "city")
    //市
    private String city;
    @Column(name = "county")
    //县
    private String county;
    //详细地址
    @Column(name = "detailed_address")
    private String detailedAddress;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_id")
    //一对一关联activity对象
    private Activity activity;

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getDetailedAddress() {
        return detailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress;
    }

    @Override
    public String toString() {
        return "ActivityLocation{" +
                "locationId=" + locationId +
                ", Province='" + Province + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", detailedAddress='" + detailedAddress + '\'' +
                ", activity=" + activity +
                '}';
    }
}