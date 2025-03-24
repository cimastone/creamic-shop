package com.ceramicshop.order.application.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 创建订单请求
 */
@Data
public class CreateOrderRequest {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 收货地址ID
     */
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;
    
    /**
     * 订单备注
     */
    @Size(max = 200, message = "备注最多200字")
    private String remark;
    
    /**
     * 订单项列表
     */
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemRequest> orderItems;
} 