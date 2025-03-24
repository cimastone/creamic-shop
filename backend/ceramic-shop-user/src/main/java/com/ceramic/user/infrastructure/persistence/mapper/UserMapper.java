package com.ceramic.user.infrastructure.persistence.mapper;

import com.ceramic.user.infrastructure.persistence.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 插入用户
     */
    int insert(UserDO user);
    
    /**
     * 更新用户
     */
    int update(UserDO user);
    
    /**
     * 根据ID查询用户
     */
    UserDO selectById(@Param("id") Long id);
    
    /**
     * 根据用户名查询用户
     */
    UserDO selectByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     */
    UserDO selectByEmail(@Param("email") String email);
    
    /**
     * 查询所有用户
     */
    List<UserDO> selectAll();
    
    /**
     * 分页查询用户
     */
    List<UserDO> selectByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计用户总数
     */
    long count();
    
    /**
     * 删除用户
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查用户名是否存在
     */
    int countByUsername(@Param("username") String username);
    
    /**
     * 检查邮箱是否存在
     */
    int countByEmail(@Param("email") String email);
} 