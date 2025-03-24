package com.ceramic.user.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 手机号值对象
 */
@Getter
@EqualsAndHashCode
public class Phone {

    private final String value;
    
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    public Phone(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (!value.matches(PHONE_REGEX)) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
        this.value = value;
    }
} 