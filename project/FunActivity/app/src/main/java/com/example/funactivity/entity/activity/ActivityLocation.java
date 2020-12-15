package com.example.funactivity.entity.activity;

public class ActivityLocation {
    //数据库表自增id
    private Integer locationId;
    //省
    private String province;
    //市
    private String city;
    //县
    private String county;
    //详细地址
    private String detailedAddress;
    //一对一关联activity对象
    private Activity activity;

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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
        return getProvince() != null ? province + "  " + city + "  " + county + " " : city + " " + county;
    }
}