package com.ceramic.user.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 密码值对象
 */
@Getter
@EqualsAndHashCode
@ToString(exclude = "value") // 不在toString中输出密码内容，避免泄露
public class Password {
    private final String value;
    
    public Password(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (value.length() < 6 || value.length() > 100) {
            throw new IllegalArgumentException("Password must be between 6 and 100 characters");
        }
        this.value = value;
    }
} 