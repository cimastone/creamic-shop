package com.ceramic.product.domain.service;

import com.ceramic.product.domain.model.aggregate.Product;
import com.ceramic.product.domain.model.valueobject.ProductId;
import com.ceramic.product.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 产品领域服务
 * 包含跨实体的业务逻辑
 */
@Service
public class ProductDomainService {
    
    private final ProductRepository productRepository;
    
    public ProductDomainService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * 检查并扣减库存
     * 
     * @param productId 产品ID
     * @param quantity 数量
     * @return 是否成功
     */
    public boolean checkAndDecreaseInventory(ProductId productId, int quantity) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (!optionalProduct.isPresent()) {
            return false;
        }
        
        Product product = optionalProduct.get();
        try {
            if (!product.isPurchasable(quantity)) {
                return false;
            }
            
            product.decreaseInventory(quantity);
            productRepository.save(product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 批量检查产品是否可购买
     * 
     * @param productIds 产品ID列表
     * @param quantities 对应的数量列表
     * @return 是否全部可购买
     */
    public boolean checkProductsAvailability(ProductId[] productIds, int[] quantities) {
        if (productIds.length != quantities.length) {
            throw new IllegalArgumentException("Product IDs and quantities must have the same length");
        }
        
        for (int i = 0; i < productIds.length; i++) {
            Optional<Product> optionalProduct = productRepository.findById(productIds[i]);
            if (!optionalProduct.isPresent() || !optionalProduct.get().isPurchasable(quantities[i])) {
                return false;
            }
        }
        
        return true;
    }
} 