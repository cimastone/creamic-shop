package com.ceramic.product.infrastructure.persistence.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品持久化对象
 */
@Data
public class ProductPO {
    /**
     * 产品ID
     */
    private Long id;
    
    /**
     * 产品名称
     */
    private String name;
    
    /**
     * 产品描述
     */
    private String description;
    
    /**
     * 产品价格
     */
    private BigDecimal price;
    
    /**
     * 产品分类
     */
    private String category;
    
    /**
     * 产品图片URL
     */
    private String image;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 产品状态：ONLINE-上架，OFFLINE-下架
     */
    private String status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 