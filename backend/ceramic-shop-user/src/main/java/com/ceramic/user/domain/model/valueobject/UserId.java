package com.ceramic.user.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 用户ID值对象
 */
@Getter
@EqualsAndHashCode
public class UserId {

    private final Long value;

    public UserId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        this.value = value;
    }
} 