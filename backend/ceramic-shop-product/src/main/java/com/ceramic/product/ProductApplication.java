package com.ceramic.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简化版产品服务启动类
 */
@SpringBootApplication
@ComponentScan(
    basePackages = {
        "com.ceramic.product.interfaces",
        "com.ceramic.product.application",
        "com.ceramic.product.infrastructure",
        "com.ceramic.product.domain"
    }
)
@MapperScan("com.ceramic.product.infrastructure.persistence.mapper")
@RestController
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
    
    @GetMapping("/")
    public String index() {
        return "Product Service is running...";
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}