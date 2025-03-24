package com.ceramic.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务启动类
 */
@SpringBootApplication
@ComponentScan(
    basePackages = {
        "com.ceramic.user.interfaces",
        "com.ceramic.user.application",
        "com.ceramic.user.infrastructure",
        "com.ceramic.user.domain"
    }
)
@MapperScan("com.ceramic.user.infrastructure.persistence.mapper")
@RestController
public class UserApplication {

    /**
     * 服务启动入口
     */
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public String health() {
        return "User Service is running!";
    }

    /**
     * 首页接口
     */
    @GetMapping("/")
    public String index() {
        return "Welcome to User Service!";
    }
} 