package com.ceramicshop.user.infrastructure.repository;

import com.ceramicshop.user.domain.model.ShippingAddress;
import com.ceramicshop.user.domain.repository.ShippingAddressRepository;
import com.ceramicshop.user.infrastructure.mapper.ShippingAddressMapper;
import com.ceramicshop.user.infrastructure.po.ShippingAddressPO;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 收货地址仓储实现类
 */
@Repository
public class ShippingAddressRepositoryImpl implements ShippingAddressRepository {

    private final ShippingAddressMapper shippingAddressMapper;

    public ShippingAddressRepositoryImpl(ShippingAddressMapper shippingAddressMapper) {
        this.shippingAddressMapper = shippingAddressMapper;
    }

    @Override
    public ShippingAddress save(ShippingAddress address) {
        ShippingAddressPO po = toPO(address);
        if (po.getId() == null) {
            // 新增地址
            po.setCreateTime(LocalDateTime.now());
            po.setUpdateTime(LocalDateTime.now());
            shippingAddressMapper.insert(po);
        } else {
            // 更新地址
            po.setUpdateTime(LocalDateTime.now());
            shippingAddressMapper.update(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<ShippingAddress> findById(Long id) {
        ShippingAddressPO po = shippingAddressMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<ShippingAddress> findByUserId(Long userId) {
        List<ShippingAddressPO> poList = shippingAddressMapper.selectByUserId(userId);
        return poList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ShippingAddress> findDefaultByUserId(Long userId) {
        ShippingAddressPO po = shippingAddressMapper.selectDefaultByUserId(userId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public void delete(Long id) {
        shippingAddressMapper.deleteById(id);
    }

    @Override
    public void setDefault(Long addressId, Long userId) {
        // 先取消该用户的所有默认地址
        shippingAddressMapper.cancelDefaultByUserId(userId);
        // 再将指定地址设为默认
        shippingAddressMapper.setDefault(addressId, userId);
    }

    /**
     * 将领域模型转换为PO
     */
    private ShippingAddressPO toPO(ShippingAddress address) {
        ShippingAddressPO po = new ShippingAddressPO();
        po.setId(address.getId());
        po.setUserId(address.getUserId());
        po.setReceiverName(address.getReceiverName());
        po.setReceiverPhone(address.getReceiverPhone());
        po.setProvince(address.getProvince());
        po.setCity(address.getCity());
        po.setDistrict(address.getDistrict());
        po.setDetailAddress(address.getDetailAddress());
        po.setIsDefault(address.isDefault());
        po.setCreateTime(address.getCreateTime());
        po.setUpdateTime(address.getUpdateTime());
        return po;
    }

    /**
     * 将PO转换为领域模型
     */
    private ShippingAddress toDomain(ShippingAddressPO po) {
        return ShippingAddress.builder()
                .id(po.getId())
                .userId(po.getUserId())
                .receiverName(po.getReceiverName())
                .receiverPhone(po.getReceiverPhone())
                .province(po.getProvince())
                .city(po.getCity())
                .district(po.getDistrict())
                .detailAddress(po.getDetailAddress())
                .isDefault(po.getIsDefault())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }
} 