package com.ceramicshop.order.infrastructure.persistence.repository;

import com.ceramicshop.order.domain.model.ShippingAddress;
import com.ceramicshop.order.domain.repository.ShippingAddressRepository;
import com.ceramicshop.order.infrastructure.persistence.entity.ShippingAddressPO;
import com.ceramicshop.order.infrastructure.persistence.mapper.ShippingAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 物流地址仓储实现类
 */
@Repository
public class ShippingAddressRepositoryImpl implements ShippingAddressRepository {

    private final ShippingAddressMapper shippingAddressMapper;

    @Autowired
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
    public Optional<ShippingAddress> findByOrderId(Long orderId) {
        ShippingAddressPO po = shippingAddressMapper.selectByOrderId(orderId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public void delete(Long id) {
        shippingAddressMapper.deleteById(id);
    }

    /**
     * 将领域模型转换为PO
     */
    private ShippingAddressPO toPO(ShippingAddress address) {
        ShippingAddressPO po = new ShippingAddressPO();
        po.setId(address.getId());
        po.setOrderId(address.getOrderId());
        po.setReceiverName(address.getReceiverName());
        po.setReceiverPhone(address.getReceiverPhone());
        po.setProvince(address.getProvince());
        po.setCity(address.getCity());
        po.setDistrict(address.getDistrict());
        po.setDetailAddress(address.getDetailAddress());
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
                .orderId(po.getOrderId())
                .receiverName(po.getReceiverName())
                .receiverPhone(po.getReceiverPhone())
                .province(po.getProvince())
                .city(po.getCity())
                .district(po.getDistrict())
                .detailAddress(po.getDetailAddress())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }
} 