package com.ceramic.user.domain.repository;

import com.ceramic.user.domain.model.aggregate.User;
import com.ceramic.user.domain.model.entity.Address;
import com.ceramic.user.domain.model.valueobject.AddressId;
import com.ceramic.user.domain.model.valueobject.Email;
import com.ceramic.user.domain.model.valueobject.UserId;
import com.ceramic.user.domain.model.valueobject.Username;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 */
public interface UserRepository {
    
    /**
     * 保存用户
     */
    User save(User user);
    
    /**
     * 根据ID查找用户
     */
    Optional<User> findById(UserId userId);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(Username username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(Email email);
    
    /**
     * 查询所有用户
     */
    List<User> findAll();
    
    /**
     * 分页查询用户
     */
    List<User> findByPage(int page, int size);
    
    /**
     * 计算用户总数
     */
    long count();
    
    /**
     * 删除用户
     */
    void delete(UserId userId);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(Username username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(Email email);

    /**
     * 保存地址
     */
    Address saveAddress(Address address);

    /**
     * 获取用户的所有地址
     */
    List<Address> findAddressesByUserId(UserId userId);

    /**
     * 获取用户的默认地址
     */
    Optional<Address> findDefaultAddressByUserId(UserId userId);

    /**
     * 获取地址
     */
    Optional<Address> findAddressById(AddressId addressId);

    /**
     * 删除地址
     */
    void deleteAddress(AddressId addressId);

    /**
     * 取消用户所有地址的默认标识
     */
    void cancelAllDefaultAddresses(UserId userId);
} 