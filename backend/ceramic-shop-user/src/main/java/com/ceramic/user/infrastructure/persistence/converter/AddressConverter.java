package com.ceramic.user.infrastructure.persistence.converter;

import com.ceramic.user.domain.model.entity.Address;
import com.ceramic.user.domain.model.valueobject.AddressId;
import com.ceramic.user.domain.model.valueobject.UserId;
import com.ceramic.user.infrastructure.persistence.entity.AddressDO;

/**
 * 地址数据转换器
 */
public class AddressConverter {

    /**
     * 领域模型转持久化实体
     */
    public static AddressDO toDataObject(Address address) {
        if (address == null) {
            return null;
        }
        
        AddressDO addressDO = new AddressDO();
        if (address.getId() != null) {
            addressDO.setId(address.getId().getValue());
        }
        addressDO.setUserId(address.getUserId().getValue());
        addressDO.setReceiverName(address.getReceiverName());
        addressDO.setReceiverPhone(address.getReceiverPhone());
        addressDO.setProvince(address.getProvince());
        addressDO.setCity(address.getCity());
        addressDO.setDistrict(address.getDistrict());
        addressDO.setDetailAddress(address.getDetailAddress());
        addressDO.setIsDefault(address.isDefault());
        addressDO.setCreateTime(address.getCreateTime());
        addressDO.setUpdateTime(address.getUpdateTime());
        return addressDO;
    }

    /**
     * 持久化实体转领域模型
     */
    public static Address toDomain(AddressDO addressDO) {
        if (addressDO == null) {
            return null;
        }
        
        return Address.rebuild(
                new AddressId(addressDO.getId()),
                new UserId(addressDO.getUserId()),
                addressDO.getReceiverName(),
                addressDO.getReceiverPhone(),
                addressDO.getProvince(),
                addressDO.getCity(),
                addressDO.getDistrict(),
                addressDO.getDetailAddress(),
                addressDO.getIsDefault(),
                addressDO.getCreateTime(),
                addressDO.getUpdateTime()
        );
    }
} 