package com.ceramic.product.application.dto;

import lombok.Data;

/**
 * 产品查询参数
 */
@Data
public class ProductQueryParams {
    
    /**
     * 产品名称(模糊查询)
     */
    private String name;
    
    /**
     * 产品分类
     */
    private String category;
    
    /**
     * 最低价格
     */
    private Double minPrice;
    
    /**
     * 最高价格
     */
    private Double maxPrice;
    
    /**
     * 产品状态
     */
    private String status;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
    
    /**
     * 排序字段
     */
    private String orderBy;
    
    /**
     * 排序方式(asc/desc)
     */
    private String sort;
} 