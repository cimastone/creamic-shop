package com.ceramicshop.order.application.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 订单项请求
 */
@Data
public class OrderItemRequest {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    
    /**
     * 商品图片
     */
    private String productImage;
    
    /**
     * 商品规格
     */
    private String productSpecs;
    
    /**
     * 商品单价 (旧字段名，为向后兼容保留)
     */
    @NotNull(message = "商品单价不能为空")
    @Positive(message = "商品单价必须大于0")
    private BigDecimal unitPrice;
    
    /**
     * 商品单价 (新字段名，与数据库一致)
     */
    private BigDecimal price;
    
    /**
     * 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量最少为1")
    private Integer quantity;
} 