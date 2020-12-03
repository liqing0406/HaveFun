package com.hebtu.havefun.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/21 15:34
 * @projectName HaveFun
 * @className WebMvcConfig.java
 * @description TODO
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/localPictures/**").addResourceLocations("file:/HaveFunResources/pictures/");
        registry.addResourceHandler("/localPictures/**").addResourceLocations("file:/home/HaveFunResources/pictures/");
    }
}
