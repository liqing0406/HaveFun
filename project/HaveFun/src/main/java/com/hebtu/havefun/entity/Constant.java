package com.hebtu.havefun.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/9 8:23
 * @projectName HaveFun
 * @className Constant.java
 * @description TODO
 */
@Component
@ConfigurationProperties(prefix = "constant")
public class Constant {
    private String serverUrl;
    private String uploadPath;
    private String serverMappingPath;
    private Integer userId;
    private String downloadPath;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getServerMappingPath() {
        return serverMappingPath;
    }

    public void setServerMappingPath(String serverMappingPath) {
        this.serverMappingPath = serverMappingPath;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    @Override
    public String toString() {
        return "Constant{" +
                "serverUrl='" + serverUrl + '\'' +
                ", uploadPath='" + uploadPath + '\'' +
                ", serverMappingPath='" + serverMappingPath + '\'' +
                ", userId=" + userId +
                ", downloadPath='" + downloadPath + '\'' +
                '}';
    }
}
