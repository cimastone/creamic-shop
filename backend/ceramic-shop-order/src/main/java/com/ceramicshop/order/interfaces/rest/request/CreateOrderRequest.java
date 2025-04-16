package com.ceramicshop.order.interfaces.rest.request;

import com.ceramicshop.order.application.dto.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建订单请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    /**
     * 收货地址ID (必填)
     */
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;
    
    /**
     * 订单商品列表
     */
    private List<OrderItemDTO> items = new ArrayList<>();
} 