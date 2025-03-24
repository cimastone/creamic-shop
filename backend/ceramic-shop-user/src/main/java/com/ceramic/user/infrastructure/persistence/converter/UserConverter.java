package com.ceramic.user.infrastructure.persistence.converter;

import com.ceramic.user.domain.model.aggregate.User;
import com.ceramic.user.domain.model.valueobject.*;
import com.ceramic.user.infrastructure.persistence.entity.UserDO;

/**
 * 用户数据转换器
 */
public class UserConverter {

    /**
     * 领域模型转持久化实体
     */
    public static UserDO toDataObject(User user) {
        if (user == null) {
            return null;
        }
        
        UserDO userDO = new UserDO();
        if (user.getId() != null) {
            userDO.setId(user.getId().getValue());
        }
        userDO.setUsername(user.getUsername().getValue());
        userDO.setPassword(user.getPassword().getValue());
        userDO.setNickname(user.getNickname());
        userDO.setEmail(user.getEmail().getValue());
        if (user.getPhone() != null) {
            userDO.setPhone(user.getPhone().getValue());
        }
        userDO.setAvatar(user.getAvatar());
        userDO.setGender(user.getGender());
        userDO.setStatus(user.getStatus().getValue());
        userDO.setLastLoginTime(user.getLastLoginTime());
        userDO.setCreateTime(user.getCreateTime());
        userDO.setUpdateTime(user.getUpdateTime());
        return userDO;
    }

    /**
     * 持久化实体转领域模型
     */
    public static User toDomain(UserDO userDO) {
        if (userDO == null) {
            return null;
        }
        
        return User.rebuild(
                new UserId(userDO.getId()),
                new Username(userDO.getUsername()),
                new Password(userDO.getPassword()),
                userDO.getNickname(),
                new Email(userDO.getEmail()),
                userDO.getPhone() != null ? new Phone(userDO.getPhone()) : null,
                userDO.getAvatar(),
                userDO.getGender(),
                UserStatus.of(userDO.getStatus()),
                userDO.getLastLoginTime(),
                userDO.getCreateTime(),
                userDO.getUpdateTime()
        );
    }
} 