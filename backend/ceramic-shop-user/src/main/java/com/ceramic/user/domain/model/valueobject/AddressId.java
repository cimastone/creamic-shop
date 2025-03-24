package com.ceramic.user.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 地址ID值对象
 */
@Getter
@EqualsAndHashCode
public class AddressId {

    private final Long value;

    public AddressId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("地址ID不能为空");
        }
        this.value = value;
    }
} 