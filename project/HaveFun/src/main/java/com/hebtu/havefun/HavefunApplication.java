package com.hebtu.havefun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class HavefunApplication {

    public static void main(String[] args) {
        SpringApplication.run(HavefunApplication.class, args);
    }

}
