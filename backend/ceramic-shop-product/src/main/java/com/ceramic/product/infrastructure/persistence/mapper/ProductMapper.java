package com.ceramic.product.infrastructure.persistence.mapper;

import com.ceramic.product.infrastructure.persistence.entity.ProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品Mapper接口
 */
@Mapper
public interface ProductMapper {
    
    /**
     * 根据ID查询产品
     */
    ProductPO selectById(Long id);
    
    /**
     * 查询所有产品
     */
    List<ProductPO> selectAll();
    
    /**
     * 根据分类查询产品
     */
    List<ProductPO> selectByCategory(String category);
    
    /**
     * 根据名称模糊查询产品
     */
    List<ProductPO> selectByNameLike(String namePattern);
    
    /**
     * 插入产品
     */
    int insert(ProductPO product);
    
    /**
     * 更新产品
     */
    int update(ProductPO product);
    
    /**
     * 删除产品
     */
    int deleteById(Long id);
    
    /**
     * 更新产品状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 查询所有产品分类
     */
    List<String> selectAllCategories();
    
    /**
     * 更新产品库存
     */
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);
    
    /**
     * 用于悲观锁的查询
     */
    ProductPO selectByIdForUpdate(Long id);
} 