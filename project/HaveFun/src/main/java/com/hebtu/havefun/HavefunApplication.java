package com.hebtu.havefun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
//这次项目写的太麻木了，数据库表设计的太乱，各种耦合，保存一个对象结果还保存好多个，级联那些没搞懂，希望某天能觉悟

//开启缓存
@EnableCaching
@SpringBootApplication
public class HavefunApplication {

    public static void main(String[] args) {
        SpringApplication.run(HavefunApplication.class, args);
    }

}