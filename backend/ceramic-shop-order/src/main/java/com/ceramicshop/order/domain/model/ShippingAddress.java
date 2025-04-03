package com.ceramicshop.order.domain.model;

import com.ceramicshop.order.application.dto.AddressDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 物流地址领域模型
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class ShippingAddress {
    
    private Long id;
    private Long orderId;  // 关联订单ID，而不是用户ID
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    /**
     * 获取完整地址
     */
    public String getFullAddress() {
        return province + " " + city + " " + district + " " + detailAddress;
    }
    
    /**
     * 更新地址信息
     */
    public void update(String receiverName, String receiverPhone, String province, String city, 
                      String district, String detailAddress) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detailAddress = detailAddress;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 从用户地址创建物流地址
     */
    public static ShippingAddress fromAddressDTO(Long orderId, AddressDTO addressDTO) {
        return ShippingAddress.builder()
                .orderId(orderId)
                .receiverName(addressDTO.getReceiverName())
                .receiverPhone(addressDTO.getReceiverPhone())
                .province(extractProvince(addressDTO.getReceiverAddress()))
                .city(extractCity(addressDTO.getReceiverAddress()))
                .district(extractDistrict(addressDTO.getReceiverAddress()))
                .detailAddress(extractDetailAddress(addressDTO.getReceiverAddress()))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 从用户输入的地址中提取省份
     */
    private static String extractProvince(String address) {
        // 简单实现，实际项目中可能需要更复杂的解析逻辑
        String[] parts = address.split(" ", 4);
        return parts.length > 0 ? parts[0] : "";
    }
    
    /**
     * 从用户输入的地址中提取城市
     */
    private static String extractCity(String address) {
        String[] parts = address.split(" ", 4);
        return parts.length > 1 ? parts[1] : "";
    }
    
    /**
     * 从用户输入的地址中提取区/县
     */
    private static String extractDistrict(String address) {
        String[] parts = address.split(" ", 4);
        return parts.length > 2 ? parts[2] : "";
    }
    
    /**
     * 从用户输入的地址中提取详细地址
     */
    private static String extractDetailAddress(String address) {
        String[] parts = address.split(" ", 4);
        return parts.length > 3 ? parts[3] : address; // 如果无法拆分，则使用整个地址
    }
} 