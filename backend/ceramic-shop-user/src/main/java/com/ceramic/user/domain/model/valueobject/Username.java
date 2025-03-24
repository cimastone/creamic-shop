package com.ceramic.user.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 用户名值对象
 */
@Getter
@EqualsAndHashCode
public class Username {

    private final String value;

    public Username(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (value.length() < 4 || value.length() > 20) {
            throw new IllegalArgumentException("用户名长度必须在4-20个字符之间");
        }
        if (!value.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("用户名只能包含字母、数字和下划线");
        }
        this.value = value;
    }
} 