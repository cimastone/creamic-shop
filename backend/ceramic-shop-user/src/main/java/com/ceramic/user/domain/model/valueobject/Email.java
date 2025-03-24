package com.ceramic.user.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 邮箱值对象
 */
@Getter
@EqualsAndHashCode
public class Email {

    private final String value;
    
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        this.value = value;
    }
} 