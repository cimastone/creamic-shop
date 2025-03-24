package com.ceramic.user.infrastructure.persistence.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户持久化实体类
 */
@Data
public class UserDO {
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
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
     * 性别，0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 状态，0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 获取邮箱
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * 设置邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * 获取头像
     */
    public String getAvatar() {
        return avatar;
    }
    
    /**
     * 设置头像
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
} 