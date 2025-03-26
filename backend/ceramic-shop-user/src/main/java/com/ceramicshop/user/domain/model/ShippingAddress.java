package com.ceramicshop.user.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收货地址领域模型
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class ShippingAddress {
    
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private boolean isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    /**
     * 获取完整地址
     */
    public String getFullAddress() {
        return province + " " + city + " " + district + " " + detailAddress;
    }
    
    /**
     * 设置为默认地址
     */
    public void setAsDefault() {
        this.isDefault = true;
    }
    
    /**
     * 取消默认地址
     */
    public void cancelDefault() {
        this.isDefault = false;
    }
    
    /**
     * 更新地址信息
     */
    public void update(String receiverName, String receiverPhone, String province, String city, 
                      String district, String detailAddress, boolean isDefault) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
        this.updateTime = LocalDateTime.now();
    }
} 