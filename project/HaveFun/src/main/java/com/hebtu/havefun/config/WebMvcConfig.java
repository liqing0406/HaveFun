package com.hebtu.havefun.config;

import com.hebtu.havefun.entity.Constant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/21 15:34
 * @projectName HaveFun
 * @className WebMvcConfig.java
 * @description TODO
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    Constant constant;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/localPictures/**").addResourceLocations(constant.getServerMappingPath());
    }
}