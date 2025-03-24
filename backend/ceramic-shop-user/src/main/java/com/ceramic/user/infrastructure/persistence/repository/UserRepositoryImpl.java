package com.ceramic.user.infrastructure.persistence.repository;

import com.ceramic.user.domain.model.aggregate.User;
import com.ceramic.user.domain.model.entity.Address;
import com.ceramic.user.domain.model.valueobject.AddressId;
import com.ceramic.user.domain.model.valueobject.Email;
import com.ceramic.user.domain.model.valueobject.UserId;
import com.ceramic.user.domain.model.valueobject.Username;
import com.ceramic.user.domain.repository.UserRepository;
import com.ceramic.user.infrastructure.persistence.converter.AddressConverter;
import com.ceramic.user.infrastructure.persistence.converter.UserConverter;
import com.ceramic.user.infrastructure.persistence.entity.AddressDO;
import com.ceramic.user.infrastructure.persistence.entity.UserDO;
import com.ceramic.user.infrastructure.persistence.mapper.AddressMapper;
import com.ceramic.user.infrastructure.persistence.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户仓储实现类
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final AddressMapper addressMapper;

    @Autowired
    public UserRepositoryImpl(UserMapper userMapper, AddressMapper addressMapper) {
        this.userMapper = userMapper;
        this.addressMapper = addressMapper;
    }

    @Override
    public User save(User user) {
        UserDO userDO = UserConverter.toDataObject(user);
        if (userDO.getId() == null) {
            userMapper.insert(userDO);
        } else {
            userMapper.update(userDO);
        }
        return UserConverter.toDomain(userDO);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        UserDO userDO = userMapper.selectById(userId.getValue());
        return Optional.ofNullable(UserConverter.toDomain(userDO));
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        UserDO userDO = userMapper.selectByUsername(username.getValue());
        return Optional.ofNullable(UserConverter.toDomain(userDO));
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        UserDO userDO = userMapper.selectByEmail(email.getValue());
        return Optional.ofNullable(UserConverter.toDomain(userDO));
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectAll().stream()
                .map(UserConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByPage(int page, int size) {
        int offset = (page - 1) * size;
        return userMapper.selectByPage(offset, size).stream()
                .map(UserConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return userMapper.count();
    }

    @Override
    public void delete(UserId userId) {
        userMapper.deleteById(userId.getValue());
    }

    @Override
    public boolean existsByUsername(Username username) {
        return userMapper.countByUsername(username.getValue()) > 0;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userMapper.countByEmail(email.getValue()) > 0;
    }

    @Override
    public Address saveAddress(Address address) {
        AddressDO addressDO = AddressConverter.toDataObject(address);
        if (addressDO.getId() == null) {
            addressMapper.insert(addressDO);
        } else {
            addressMapper.update(addressDO);
        }
        return AddressConverter.toDomain(addressDO);
    }

    @Override
    public List<Address> findAddressesByUserId(UserId userId) {
        return addressMapper.selectByUserId(userId.getValue()).stream()
                .map(AddressConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Address> findDefaultAddressByUserId(UserId userId) {
        AddressDO addressDO = addressMapper.selectDefaultByUserId(userId.getValue());
        return Optional.ofNullable(AddressConverter.toDomain(addressDO));
    }

    @Override
    public Optional<Address> findAddressById(AddressId addressId) {
        AddressDO addressDO = addressMapper.selectById(addressId.getValue());
        return Optional.ofNullable(AddressConverter.toDomain(addressDO));
    }

    @Override
    public void deleteAddress(AddressId addressId) {
        addressMapper.deleteById(addressId.getValue());
    }

    @Override
    public void cancelAllDefaultAddresses(UserId userId) {
        addressMapper.cancelAllDefaultByUserId(userId.getValue());
    }
} 