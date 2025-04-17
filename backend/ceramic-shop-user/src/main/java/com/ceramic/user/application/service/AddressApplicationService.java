package com.ceramic.user.application.service;

import com.ceramic.user.application.dto.AddressDTO;
import com.ceramic.user.application.dto.AddressRequest;
import com.ceramic.user.domain.model.entity.Address;
import com.ceramic.user.domain.model.valueobject.AddressId;
import com.ceramic.user.domain.model.valueobject.UserId;
import com.ceramic.user.domain.service.UserDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 地址应用服务实现类
 */
@Service
public class AddressApplicationService {

    private final UserDomainService userDomainService;

    @Autowired
    public AddressApplicationService(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }

    /**
     * 添加收货地址
     */
    @Transactional
    public AddressDTO addAddress(Long userId, AddressRequest request) {
        Address address = userDomainService.addAddress(
                new UserId(userId),
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getProvince(),
                request.getCity(),
                request.getDistrict(),
                request.getDetailAddress(),
                request.getIsDefault()
        );
        return convertAddressToDTO(address);
    }

    /**
     * 更新收货地址
     */
    @Transactional
    public AddressDTO updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = userDomainService.updateAddress(
                new UserId(userId),
                new AddressId(addressId),
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getProvince(),
                request.getCity(),
                request.getDistrict(),
                request.getDetailAddress()
        );
        
        // 如果设为默认地址
        if (request.getIsDefault()) {
            address = userDomainService.setDefaultAddress(new UserId(userId), new AddressId(addressId));
        }
        
        return convertAddressToDTO(address);
    }

    /**
     * 设置默认地址
     */
    @Transactional
    public AddressDTO setDefaultAddress(Long userId, Long addressId) {
        Address address = userDomainService.setDefaultAddress(new UserId(userId), new AddressId(addressId));
        return convertAddressToDTO(address);
    }

    /**
     * 删除收货地址
     */
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        userDomainService.deleteAddress(new UserId(userId), new AddressId(addressId));
    }

    /**
     * 获取用户的所有收货地址
     */
    public List<AddressDTO> getUserAddresses(Long userId) {
        List<Address> addresses = userDomainService.getUserAddresses(new UserId(userId));
        return addresses.stream()
                .map(this::convertAddressToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的默认收货地址
     */
    public Optional<AddressDTO> getUserDefaultAddress(Long userId) {
        return userDomainService.getUserDefaultAddress(new UserId(userId))
                .map(this::convertAddressToDTO);
    }

    /**
     * 获取地址详情
     */
    public AddressDTO getAddressById(Long userId, Long addressId) {
        Optional<Address> addressOpt = userDomainService.getAddressById(new UserId(userId), new AddressId(addressId));
        return addressOpt
                .map(this::convertAddressToDTO)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));
    }

    /**
     * 将地址实体转换为DTO
     */
    private AddressDTO convertAddressToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId().getValue());
        dto.setUserId(address.getUserId().getValue());
        dto.setReceiverName(address.getReceiverName());
        dto.setReceiverPhone(address.getReceiverPhone());
        dto.setProvince(address.getProvince());
        dto.setCity(address.getCity());
        dto.setDistrict(address.getDistrict());
        dto.setDetailAddress(address.getDetailAddress());
        dto.setIsDefault(address.isDefault());
        dto.setCreatedAt(address.getCreateTime());
        dto.setUpdatedAt(address.getUpdateTime());
        return dto;
    }
} 