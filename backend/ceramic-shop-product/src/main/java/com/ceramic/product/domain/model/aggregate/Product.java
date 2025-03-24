package com.ceramic.product.domain.model.aggregate;

import com.ceramic.product.domain.model.valueobject.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 产品聚合根
 */
@Getter
public class Product {
    private ProductId id;
    private ProductName name;
    private ProductDescription description;
    private Money price;
    private Category category;
    private ImageUrl image;
    private Inventory inventory;
    private ProductStatus status;
    
    @Setter(AccessLevel.PACKAGE)
    private Date createTime;
    
    @Setter(AccessLevel.PACKAGE)
    private Date updateTime;
    
    // 私有构造函数，通过Builder或Factory创建
    private Product() {}
    
    /**
     * 产品上架
     * 上架前需确保有库存
     */
    public void online() {
        if (!inventory.isAvailable()) {
            throw new IllegalStateException("Cannot put product online with zero inventory");
        }
        this.status = ProductStatus.ONLINE;
        this.updateTime = new Date();
    }
    
    /**
     * 产品下架
     */
    public void offline() {
        this.status = ProductStatus.OFFLINE;
        this.updateTime = new Date();
    }
    
    /**
     * 更新产品价格
     */
    public void updatePrice(Money newPrice) {
        if (newPrice.isLessThan(Money.ZERO) || newPrice.isZero()) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.price = newPrice;
        this.updateTime = new Date();
    }
    
    /**
     * 更新产品库存
     */
    public void updateInventory(Inventory newInventory) {
        this.inventory = newInventory;
        this.updateTime = new Date();
    }
    
    /**
     * 减少库存
     */
    public void decreaseInventory(int quantity) {
        this.inventory = this.inventory.decrease(quantity);
        this.updateTime = new Date();
        
        // 如果库存为0，自动下架产品
        if (!this.inventory.isAvailable() && this.status == ProductStatus.ONLINE) {
            this.offline();
        }
    }
    
    /**
     * 增加库存
     */
    public void increaseInventory(int quantity) {
        this.inventory = this.inventory.increase(quantity);
        this.updateTime = new Date();
    }
    
    /**
     * 更新产品信息
     */
    public void update(ProductName name, 
                      ProductDescription description, 
                      Money price, 
                      Category category, 
                      ImageUrl image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.image = image;
        this.updateTime = new Date();
    }
    
    /**
     * 产品是否可购买
     */
    public boolean isPurchasable(int quantity) {
        return this.status == ProductStatus.ONLINE && 
               this.inventory.isAvailable(quantity);
    }
    
    /**
     * Builder模式
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder类
     */
    public static class Builder {
        private Product product = new Product();
        
        public Builder id(ProductId id) {
            product.id = id;
            return this;
        }
        
        public Builder name(ProductName name) {
            product.name = name;
            return this;
        }
        
        public Builder description(ProductDescription description) {
            product.description = description;
            return this;
        }
        
        public Builder price(Money price) {
            product.price = price;
            return this;
        }
        
        public Builder category(Category category) {
            product.category = category;
            return this;
        }
        
        public Builder image(ImageUrl image) {
            product.image = image;
            return this;
        }
        
        public Builder inventory(Inventory inventory) {
            product.inventory = inventory;
            return this;
        }
        
        public Builder status(ProductStatus status) {
            product.status = status;
            return this;
        }
        
        public Builder createTime(Date createTime) {
            product.createTime = createTime;
            return this;
        }
        
        public Builder updateTime(Date updateTime) {
            product.updateTime = updateTime;
            return this;
        }
        
        public Product build() {
            // 校验产品对象
            validate(product);
            
            if (product.createTime == null) {
                product.createTime = new Date();
            }
            if (product.updateTime == null) {
                product.updateTime = new Date();
            }
            return product;
        }
        
        private void validate(Product product) {
            if (product.name == null) {
                throw new IllegalArgumentException("Product name cannot be null");
            }
            if (product.price == null) {
                throw new IllegalArgumentException("Product price cannot be null");
            }
            if (product.category == null) {
                throw new IllegalArgumentException("Product category cannot be null");
            }
            if (product.inventory == null) {
                product.inventory = new Inventory(0);
            }
            if (product.status == null) {
                product.status = ProductStatus.OFFLINE;
            }
        }
    }
} 