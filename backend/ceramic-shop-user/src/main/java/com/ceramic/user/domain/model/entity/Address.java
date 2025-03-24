package com.ceramic.user.domain.model.entity;

import com.ceramic.user.domain.model.valueobject.AddressId;
import com.ceramic.user.domain.model.valueobject.UserId;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 地址实体
 */
@Getter
public class Address {
    private AddressId id;
    private UserId userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private boolean isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Address() {
    }

    /**
     * 创建新地址
     */
    public static Address create(
            UserId userId,
            String receiverName,
            String receiverPhone,
            String province,
            String city,
            String district,
            String detailAddress,
            boolean isDefault
    ) {
        Address address = new Address();
        address.userId = userId;
        address.receiverName = receiverName;
        address.receiverPhone = receiverPhone;
        address.province = province;
        address.city = city;
        address.district = district;
        address.detailAddress = detailAddress;
        address.isDefault = isDefault;
        address.createTime = LocalDateTime.now();
        address.updateTime = LocalDateTime.now();
        return address;
    }

    /**
     * 重建地址实体
     */
    public static Address rebuild(
            AddressId id,
            UserId userId,
            String receiverName,
            String receiverPhone,
            String province,
            String city,
            String district,
            String detailAddress,
            boolean isDefault,
            LocalDateTime createTime,
            LocalDateTime updateTime
    ) {
        Address address = new Address();
        address.id = id;
        address.userId = userId;
        address.receiverName = receiverName;
        address.receiverPhone = receiverPhone;
        address.province = province;
        address.city = city;
        address.district = district;
        address.detailAddress = detailAddress;
        address.isDefault = isDefault;
        address.createTime = createTime;
        address.updateTime = updateTime;
        return address;
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
     * 设置为默认地址
     */
    public void setAsDefault() {
        this.isDefault = true;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 取消默认地址
     */
    public void cancelDefault() {
        this.isDefault = false;
        this.updateTime = LocalDateTime.now();
    }
} 