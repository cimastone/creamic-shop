package com.ceramic.user.domain.model.valueobject;

/**
 * 用户状态枚举
 */
public enum UserStatus {
    /**
     * 启用
     */
    ENABLED(1),
    
    /**
     * 禁用
     */
    DISABLED(0);
    
    private final int value;
    
    UserStatus(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static UserStatus fromValue(int value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown user status value: " + value);
    }
    
    /**
     * 根据值获取状态
     */
    public static UserStatus of(Integer value) {
        if (value == null) {
            return DISABLED;
        }
        return fromValue(value);
    }
} 