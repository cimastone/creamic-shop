package com.ceramicshop.user.application.impl;

import com.ceramicshop.user.application.ShippingAddressService;
import com.ceramicshop.user.application.dto.ShippingAddressDTO;
import com.ceramicshop.user.domain.model.ShippingAddress;
import com.ceramicshop.user.domain.repository.ShippingAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {

    private final ShippingAddressRepository addressRepository;

    @Autowired
    public ShippingAddressServiceImpl(ShippingAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public ShippingAddressDTO createAddress(Long userId, ShippingAddressDTO addressDTO) {
        boolean isDefault = addressDTO.isDefault();
        
        // 如果设为默认地址，或者是用户的第一个地址，则设为默认
        if (!isDefault) {
            List<ShippingAddress> addresses = addressRepository.findByUserId(userId);
            if (addresses.isEmpty()) {
                isDefault = true;
            }
        }
        
        ShippingAddress address = ShippingAddress.builder()
                .userId(userId)
                .receiverName(addressDTO.getReceiverName())
                .receiverPhone(addressDTO.getReceiverPhone())
                .province(addressDTO.getProvince())
                .city(addressDTO.getCity())
                .district(addressDTO.getDistrict())
                .detailAddress(addressDTO.getDetailAddress())
                .isDefault(isDefault)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        
        // 如果是默认地址，则取消其他默认地址
        if (isDefault) {
            addressRepository.findDefaultByUserId(userId).ifPresent(defaultAddress -> {
                defaultAddress.cancelDefault();
                addressRepository.save(defaultAddress);
            });
        }
        
        address = addressRepository.save(address);
        return convertToDTO(address);
    }

    @Override
    @Transactional
    public ShippingAddressDTO updateAddress(Long id, ShippingAddressDTO addressDTO) {
        ShippingAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));
        
        boolean isDefault = addressDTO.isDefault();
        
        address.update(
            addressDTO.getReceiverName(),
            addressDTO.getReceiverPhone(),
            addressDTO.getProvince(),
            addressDTO.getCity(),
            addressDTO.getDistrict(),
            addressDTO.getDetailAddress(),
            isDefault
        );
        
        // 如果设为默认地址，则取消其他默认地址
        if (isDefault) {
            addressRepository.setDefault(id, address.getUserId());
        }
        
        address = addressRepository.save(address);
        return convertToDTO(address);
    }

    @Override
    public ShippingAddressDTO getAddressById(Long id) {
        ShippingAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));
        return convertToDTO(address);
    }

    @Override
    public List<ShippingAddressDTO> getAddressesByUserId(Long userId) {
        List<ShippingAddress> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        ShippingAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));
        
        // 如果删除的是默认地址，且有其他地址，则将另一个地址设为默认
        if (address.isDefault()) {
            List<ShippingAddress> addresses = addressRepository.findByUserId(address.getUserId());
            if (addresses.size() > 1) {
                // 找到一个非当前地址的地址设为默认
                ShippingAddress newDefault = addresses.stream()
                        .filter(a -> !a.getId().equals(id))
                        .findFirst()
                        .orElse(null);
                
                if (newDefault != null) {
                    newDefault.setAsDefault();
                    addressRepository.save(newDefault);
                }
            }
        }
        
        addressRepository.delete(id);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long id, Long userId) {
        addressRepository.setDefault(id, userId);
    }

    @Override
    public ShippingAddressDTO getDefaultAddress(Long userId) {
        return addressRepository.findDefaultByUserId(userId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * 将领域模型转换为DTO
     */
    private ShippingAddressDTO convertToDTO(ShippingAddress address) {
        return ShippingAddressDTO.builder()
                .id(address.getId())
                .userId(address.getUserId())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .detailAddress(address.getDetailAddress())
                .fullAddress(address.getFullAddress())
                .isDefault(address.isDefault())
                .createTime(address.getCreateTime())
                .updateTime(address.getUpdateTime())
                .build();
    }
} 