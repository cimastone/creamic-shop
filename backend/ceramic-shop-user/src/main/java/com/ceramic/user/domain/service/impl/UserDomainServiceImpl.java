package com.ceramic.user.domain.service.impl;

import com.ceramic.user.domain.model.aggregate.User;
import com.ceramic.user.domain.model.entity.Address;
import com.ceramic.user.domain.model.valueobject.*;
import com.ceramic.user.domain.repository.UserRepository;
import com.ceramic.user.domain.service.UserDomainService;
import com.ceramic.user.infrastructure.util.PasswordEncoder;

import lombok.extern.java.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户领域服务实现类
 */
@Service
@Log
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDomainServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 注册用户
     */
    @Override
    public User registerUser(String usernameStr, String passwordStr, String emailStr, String phoneStr, String nickname) {
        // 创建值对象
        Username username = new Username(usernameStr);
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 创建其他值对象
        String encodedPassword = passwordEncoder.encode(passwordStr);
        Password password = new Password(encodedPassword);
        Email email = new Email(emailStr);
        Phone phone = phoneStr != null ? new Phone(phoneStr) : null;
        
        // 创建用户
        User user = User.register(username, password, email, phone, nickname);

        // 保存用户
        user = userRepository.save(user);
        System.out.println(user.toString());
        return user;
    }

    /**
     * 用户登录
     */
    @Override
    public Optional<User> login(Username username, Password password) {
        // 根据用户名查找用户
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        // 验证密码
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (passwordEncoder.matches(password.getValue(), user.getPassword().getValue())) {
                // 记录登录时间
                user.recordLogin();
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    /**
     * 根据ID获取用户
     */
    @Override
    public Optional<User> getUserById(UserId userId) {
        return userRepository.findById(userId);
    }

    /**
     * 根据用户名获取用户
     */
    @Override
    public Optional<User> getUserByUsername(Username username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 检查用户是否存在
     */
    @Override
    public boolean checkUserExists(Username username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否已被注册
     */
    @Override
    public boolean existsByEmail(Email email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 更新用户资料
     */
    @Override
    public User updateUserProfile(UserId userId, String nickname, String emailStr, String phoneStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        Phone phone = phoneStr != null ? new Phone(phoneStr) : null;
        Email email = new Email(emailStr);
        
        user.updateProfile(nickname, phone, user.getGender(), user.getAvatar());
        return userRepository.save(user);
    }

    /**
     * 修改密码
     */
    @Override
    public User changePassword(UserId userId, Password oldPassword, Password newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword.getValue(), user.getPassword().getValue())) {
            throw new IllegalArgumentException("旧密码不正确");
        }

        user.changePassword(newPassword);
        return userRepository.save(user);
    }

    /**
     * 添加收货地址
     */
    @Override
    public Address addAddress(UserId userId, String receiverName, String receiverPhone, String province,
                              String city, String district, String detailAddress, boolean isDefault) {
        // 确认用户存在
        if (!userRepository.findById(userId).isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 如果设置为默认地址，则清除其他默认地址
        if (isDefault) {
            userRepository.cancelAllDefaultAddresses(userId);
        }

        // 创建地址
        Address address = Address.create(userId, receiverName, receiverPhone, province, city, district, detailAddress, isDefault);
        
        // 保存地址
        return userRepository.saveAddress(address);
    }

    /**
     * 更新收货地址
     */
    @Override
    public Address updateAddress(UserId userId, AddressId addressId, String receiverName, String receiverPhone,
                                 String province, String city, String district, String detailAddress) {
        Address address = userRepository.findAddressById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));

        // 确认地址归属于该用户
        if (!address.getUserId().getValue().equals(userId.getValue())) {
            throw new IllegalArgumentException("地址不属于该用户");
        }

        // 更新地址
        address.update(receiverName, receiverPhone, province, city, district, detailAddress);
        return userRepository.saveAddress(address);
    }

    /**
     * 设置默认地址
     */
    @Override
    public Address setDefaultAddress(UserId userId, AddressId addressId) {
        Address address = userRepository.findAddressById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));

        // 确认地址归属于该用户
        if (!address.getUserId().getValue().equals(userId.getValue())) {
            throw new IllegalArgumentException("地址不属于该用户");
        }

        // 取消所有默认地址
        userRepository.cancelAllDefaultAddresses(userId);

        // 设置为默认地址
        address.setAsDefault();
        return userRepository.saveAddress(address);
    }

    /**
     * 删除收货地址
     */
    @Override
    public void deleteAddress(UserId userId, AddressId addressId) {
        Address address = userRepository.findAddressById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));

        // 确认地址归属于该用户
        if (!address.getUserId().getValue().equals(userId.getValue())) {
            throw new IllegalArgumentException("地址不属于该用户");
        }

        userRepository.deleteAddress(addressId);
    }

    /**
     * 获取用户的所有收货地址
     */
    @Override
    public List<Address> getUserAddresses(UserId userId) {
        return userRepository.findAddressesByUserId(userId);
    }

    /**
     * 获取用户的默认收货地址
     */
    @Override
    public Optional<Address> getUserDefaultAddress(UserId userId) {
        return userRepository.findDefaultAddressByUserId(userId);
    }

    /**
     * 根据ID获取地址
     */
    @Override
    public Optional<Address> getAddressById(UserId userId, AddressId addressId) {
        Optional<Address> addressOpt = userRepository.findAddressById(addressId);
        
        // 检查地址是否存在，且属于指定用户
        if (addressOpt.isPresent()) {
            Address address = addressOpt.get();
            if (address.getUserId().getValue().equals(userId.getValue())) {
                return addressOpt;
            }
        }
        
        return Optional.empty();
    }
} 