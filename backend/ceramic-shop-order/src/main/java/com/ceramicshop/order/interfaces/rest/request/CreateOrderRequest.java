package com.ceramicshop.order.interfaces.rest.request;

import com.ceramicshop.order.application.dto.OrderItemDTO;

import java.util.List;

/**
 * 创建订单请求
 */
public class CreateOrderRequest {
    
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private List<OrderItemDTO> items;
    
    public String getReceiverName() {
        return receiverName;
    }
    
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    
    public String getReceiverPhone() {
        return receiverPhone;
    }
    
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }
    
    public String getReceiverAddress() {
        return receiverAddress;
    }
    
    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
    
    public List<OrderItemDTO> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
} 