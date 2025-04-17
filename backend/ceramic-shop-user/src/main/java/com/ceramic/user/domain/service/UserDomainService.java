package com.ceramic.user.domain.service;

import com.ceramic.user.domain.model.aggregate.User;
import com.ceramic.user.domain.model.entity.Address;
import com.ceramic.user.domain.model.valueobject.*;

import java.util.List;
import java.util.Optional;

/**
 * 用户领域服务接口
 */
public interface UserDomainService {

    /**
     * 注册用户
     */
    User registerUser(String username, String password, String email, String phone, String nickname);

    /**
     * 用户登录
     */
    Optional<User> login(Username username, Password password);

    /**
     * 根据ID获取用户
     */
    Optional<User> getUserById(UserId userId);

    /**
     * 根据用户名获取用户
     */
    Optional<User> getUserByUsername(Username username);

    /**
     * 检查用户是否存在
     */
    boolean checkUserExists(Username username);

    /**
     * 检查邮箱是否已被注册
     */
    boolean existsByEmail(Email email);

    /**
     * 更新用户资料
     */
    User updateUserProfile(UserId userId, String nickname, String email, String phone);

    /**
     * 修改密码
     */
    User changePassword(UserId userId, Password oldPassword, Password newPassword);

    /**
     * 添加收货地址
     */
    Address addAddress(UserId userId, String receiverName, String receiverPhone, String province, 
                       String city, String district, String detailAddress, boolean isDefault);

    /**
     * 更新收货地址
     */
    Address updateAddress(UserId userId, AddressId addressId, String receiverName, String receiverPhone, 
                          String province, String city, String district, String detailAddress);

    /**
     * 设置默认地址
     */
    Address setDefaultAddress(UserId userId, AddressId addressId);

    /**
     * 删除收货地址
     */
    void deleteAddress(UserId userId, AddressId addressId);

    /**
     * 获取用户的所有收货地址
     */
    List<Address> getUserAddresses(UserId userId);

    /**
     * 获取用户的默认收货地址
     */
    Optional<Address> getUserDefaultAddress(UserId userId);
    
    /**
     * 根据ID获取地址
     */
    Optional<Address> getAddressById(UserId userId, AddressId addressId);
} 