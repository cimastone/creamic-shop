package com.ceramic.product.domain.repository;

import com.ceramic.product.domain.model.aggregate.Product;
import com.ceramic.product.domain.model.valueobject.Category;
import com.ceramic.product.domain.model.valueobject.ProductId;

import java.util.List;
import java.util.Optional;

/**
 * 产品仓储接口
 */
public interface ProductRepository {
    
    /**
     * 保存产品
     * 
     * @param product 产品聚合
     * @return 保存后的产品（可能包含新生成的ID）
     */
    Product save(Product product);
    
    /**
     * 根据ID查找产品
     * 
     * @param id 产品ID
     * @return 产品聚合，不存在则返回空
     */
    Optional<Product> findById(ProductId id);
    
    /**
     * 查找所有产品
     * 
     * @return 产品列表
     */
    List<Product> findAll();
    
    /**
     * 根据类别查找产品
     * 
     * @param category 产品类别
     * @return 产品列表
     */
    List<Product> findByCategory(Category category);
    
    /**
     * 删除产品
     * 
     * @param id 产品ID
     * @return 是否删除成功
     */
    boolean remove(ProductId id);
    
    /**
     * 获取所有产品类别
     * 
     * @return 类别列表
     */
    List<Category> findAllCategories();
    
    /**
     * 根据名称模糊查询产品
     * 
     * @param namePattern 名称模式
     * @return
     */
    List<Product> findByNameLike(String namePattern);
} 