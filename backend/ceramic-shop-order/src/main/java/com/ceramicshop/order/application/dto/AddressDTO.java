package com.ceramicshop.order.application.dto;

import lombok.Data;

/**
 * 地址数据传输对象
 */
@Data
public class AddressDTO {
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人电话
     */
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
} 