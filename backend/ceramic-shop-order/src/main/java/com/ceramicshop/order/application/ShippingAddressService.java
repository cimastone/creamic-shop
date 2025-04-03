package com.ceramicshop.order.application;

import com.ceramicshop.order.application.dto.AddressDTO;
import com.ceramicshop.order.application.dto.ShippingAddressDTO;
import com.ceramicshop.order.domain.model.ShippingAddress;
import com.ceramicshop.order.domain.repository.ShippingAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 物流地址应用服务
 */
@Service
public class ShippingAddressService {
    
    private final ShippingAddressRepository addressRepository;

    @Autowired
    public ShippingAddressService(ShippingAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }
    
    /**
     * 创建物流地址
     */
    @Transactional
    public ShippingAddressDTO createAddress(Long orderId, AddressDTO addressDTO) {
        ShippingAddress address = ShippingAddress.fromAddressDTO(orderId, addressDTO);
        address = addressRepository.save(address);
        return convertToDTO(address);
    }
    
    /**
     * 根据订单ID获取物流地址
     */
    public Optional<ShippingAddressDTO> getAddressByOrderId(Long orderId) {
        return addressRepository.findByOrderId(orderId).map(this::convertToDTO);
    }
    
    /**
     * 根据ID获取物流地址
     */
    public ShippingAddressDTO getAddressById(Long id) {
        ShippingAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("物流地址不存在"));
        return convertToDTO(address);
    }
    
    /**
     * 更新物流地址
     */
    @Transactional
    public ShippingAddressDTO updateAddress(Long id, ShippingAddressDTO addressDTO) {
        ShippingAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("物流地址不存在"));
        
        address.update(
            addressDTO.getReceiverName(),
            addressDTO.getReceiverPhone(),
            addressDTO.getProvince(),
            addressDTO.getCity(),
            addressDTO.getDistrict(),
            addressDTO.getDetailAddress()
        );
        
        address = addressRepository.save(address);
        return convertToDTO(address);
    }
    
    /**
     * 删除物流地址
     */
    @Transactional
    public void deleteAddress(Long id) {
        addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("物流地址不存在"));
        addressRepository.delete(id);
    }
    
    /**
     * 将领域模型转换为DTO
     */
    private ShippingAddressDTO convertToDTO(ShippingAddress address) {
        return ShippingAddressDTO.builder()
                .id(address.getId())
                .orderId(address.getOrderId())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .detailAddress(address.getDetailAddress())
                .fullAddress(address.getFullAddress())
                .createTime(address.getCreateTime())
                .updateTime(address.getUpdateTime())
                .build();
    }
} 