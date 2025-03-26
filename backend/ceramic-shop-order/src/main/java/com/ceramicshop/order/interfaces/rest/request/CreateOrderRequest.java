package com.ceramicshop.order.interfaces.rest.request;

import com.ceramicshop.order.application.dto.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建订单请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    // 方式1：通过地址ID创建订单
    private Long addressId;
    
    // 方式2：直接提供收货人信息创建订单（备选方案）
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    
    private List<OrderItemDTO> items = new ArrayList<>();
    
    public Long getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
    
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
    
    /**
     * 判断是否直接提供了收货人信息
     */
    public boolean hasReceiverInfo() {
        return receiverName != null && !receiverName.isEmpty() &&
               receiverPhone != null && !receiverPhone.isEmpty() &&
               receiverAddress != null && !receiverAddress.isEmpty();
    }
} 