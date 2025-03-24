package com.ceramic.user.infrastructure.persistence.mapper;

import com.ceramic.user.infrastructure.persistence.entity.AddressDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 地址Mapper接口
 */
@Mapper
public interface AddressMapper {
    
    /**
     * 插入地址
     */
    int insert(AddressDO address);
    
    /**
     * 更新地址
     */
    int update(AddressDO address);
    
    /**
     * 根据ID查询地址
     */
    AddressDO selectById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询所有地址
     */
    List<AddressDO> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询默认地址
     */
    AddressDO selectDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 删除地址
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 取消用户所有地址的默认标识
     */
    int cancelAllDefaultByUserId(@Param("userId") Long userId);
} 