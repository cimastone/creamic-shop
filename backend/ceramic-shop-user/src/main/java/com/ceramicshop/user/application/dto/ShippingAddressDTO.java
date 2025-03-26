package com.ceramicshop.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收货地址数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressDTO {
    
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private String fullAddress;
    private boolean isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 