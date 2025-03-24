package com.ceramic.user.application.dto;

import com.ceramic.user.domain.model.aggregate.User;
import com.ceramic.user.domain.model.valueobject.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 角色
     */
    private String role;

    /**
     * 从领域对象转换为DTO
     */
    public static UserDTO fromDomain(User user) {
        return UserDTO.builder()
                .id(user.getId().getValue())
                .username(user.getUsername().getValue())
                .nickname(user.getNickname())
                .email(user.getEmail().getValue())
                .phone(user.getPhone() != null ? user.getPhone().getValue() : null)
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .status(user.getStatus().getValue())
                .lastLoginTime(user.getLastLoginTime())
                .createdAt(user.getCreateTime())
                .updatedAt(user.getUpdateTime())
                .role(user.getRole())
                .build();
    }
} 