package com.ceramic.user.domain.repository;

import com.ceramic.user.domain.model.entity.Address;
import com.ceramic.user.domain.model.valueobject.AddressId;
import com.ceramic.user.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 地址仓储接口
 */
public interface AddressRepository {
    
    /**
     * 保存地址
     */
    Address save(Address address);
    
    /**
     * 根据ID查找地址
     */
    Optional<Address> findById(AddressId addressId);
    
    /**
     * 根据用户ID查找所有地址
     */
    List<Address> findByUserId(UserId userId);
    
    /**
     * 根据用户ID查找默认地址
     */
    Optional<Address> findDefaultByUserId(UserId userId);
    
    /**
     * 删除地址
     */
    void delete(AddressId addressId);
    
    /**
     * 删除用户的所有地址
     */
    void deleteByUserId(UserId userId);
    
    /**
     * 清除用户的所有默认地址状态
     */
    void clearDefaultByUserId(UserId userId);
} 