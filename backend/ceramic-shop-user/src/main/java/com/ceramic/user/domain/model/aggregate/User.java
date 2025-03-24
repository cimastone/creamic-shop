package com.ceramic.user.domain.model.aggregate;

import com.ceramic.user.domain.model.valueobject.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 用户聚合根
 */
@Getter
public class User {
    private UserId id;
    private Username username;
    private Password password;
    private String nickname;
    private Email email;
    private Phone phone;
    private String avatar;
    private Integer gender;
    private UserStatus status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 角色
     */
    private String role = "USER";

    private User() {
    }

    /**
     * 注册用户
     */
    public static User register(Username username, Password password, Email email, Phone phone, String nickname) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.email = email;
        user.phone = phone;
        user.nickname = nickname;
        user.gender = 0;
        user.status = UserStatus.ENABLED;
        user.createTime = LocalDateTime.now();
        user.updateTime = LocalDateTime.now();
        return user;
    }

    /**
     * 重建用户实体
     */
    public static User rebuild(
            UserId id,
            Username username,
            Password password,
            String nickname,
            Email email,
            Phone phone,
            String avatar,
            Integer gender,
            UserStatus status,
            LocalDateTime lastLoginTime,
            LocalDateTime createTime,
            LocalDateTime updateTime
    ) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.password = password;
        user.nickname = nickname;
        user.email = email;
        user.phone = phone;
        user.avatar = avatar;
        user.gender = gender;
        user.status = status;
        user.lastLoginTime = lastLoginTime;
        user.createTime = createTime;
        user.updateTime = updateTime;
        return user;
    }

    /**
     * 更新用户资料
     */
    public void updateProfile(String nickname, Phone phone, Integer gender, String avatar) {
        this.nickname = nickname;
        this.phone = phone;
        this.gender = gender;
        this.avatar = avatar;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 修改密码
     */
    public void changePassword(Password newPassword) {
        this.password = newPassword;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 记录登录时间
     */
    public void recordLogin() {
        this.lastLoginTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新用户状态
     */
    public void updateStatus(UserStatus status) {
        this.status = status;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 获取角色
     */
    public String getRole() {
        return role;
    }
} 