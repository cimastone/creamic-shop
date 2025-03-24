package com.ceramic.product.infrastructure.persistence.repository;

import com.ceramic.product.domain.model.aggregate.Product;
import com.ceramic.product.domain.model.valueobject.*;
import com.ceramic.product.domain.repository.ProductRepository;
import com.ceramic.product.infrastructure.persistence.entity.ProductPO;
import com.ceramic.product.infrastructure.persistence.mapper.ProductMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 产品仓储实现类
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    
    private final ProductMapper productMapper;
    
    public ProductRepositoryImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }
    
    @Override
    public Product save(Product product) {
        ProductPO po = toProductPO(product);
        if (product.getId() == null) {
            // 新增
            productMapper.insert(po);
            // 更新领域对象ID
            return Product.builder()
                    .id(new ProductId(po.getId()))
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .category(product.getCategory())
                    .image(product.getImage())
                    .inventory(product.getInventory())
                    .status(product.getStatus())
                    .createTime(po.getCreateTime())
                    .updateTime(po.getUpdateTime())
                    .build();
        } else {
            // 更新
            productMapper.update(po);
            return product;
        }
    }
    
    @Override
    public Optional<Product> findById(ProductId id) {
        ProductPO po = productMapper.selectById(id.getValue());
        return Optional.ofNullable(po).map(this::toProduct);
    }
    
    @Override
    public List<Product> findAll() {
        return productMapper.selectAll()
                .stream()
                .map(this::toProduct)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCategory(Category category) {
        return productMapper.selectByCategory(category.getValue())
                .stream()
                .map(this::toProduct)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean remove(ProductId id) {
        return productMapper.deleteById(id.getValue()) > 0;
    }
    
    @Override
    public List<Category> findAllCategories() {
        return productMapper.selectAllCategories()
                .stream()
                .map(Category::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByNameLike(String namePattern) {
        return productMapper.selectByNameLike(namePattern)
                .stream()
                .map(this::toProduct)
                .collect(Collectors.toList());
    }
    
    /**
     * 将持久化对象转换为领域对象
     */
    private Product toProduct(ProductPO po) {
        return Product.builder()
                .id(new ProductId(po.getId()))
                .name(new ProductName(po.getName()))
                .description(po.getDescription() != null ? new ProductDescription(po.getDescription()) : null)
                .price(new Money(po.getPrice()))
                .category(new Category(po.getCategory()))
                .image(po.getImage() != null ? new ImageUrl(po.getImage()) : null)
                .inventory(new Inventory(po.getStock()))
                .status(ProductStatus.valueOf(po.getStatus()))
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }
    
    /**
     * 将领域对象转换为持久化对象
     */
    private ProductPO toProductPO(Product product) {
        ProductPO po = new ProductPO();
        if (product.getId() != null) {
            po.setId(product.getId().getValue());
        }
        po.setName(product.getName().getValue());
        if (product.getDescription() != null) {
            po.setDescription(product.getDescription().getValue());
        }
        po.setPrice(product.getPrice().getAmount());
        po.setCategory(product.getCategory().getValue());
        if (product.getImage() != null) {
            po.setImage(product.getImage().getValue());
        }
        po.setStock(product.getInventory().getAvailableQuantity());
        po.setStatus(product.getStatus().name());
        po.setCreateTime(product.getCreateTime());
        po.setUpdateTime(product.getUpdateTime());
        return po;
    }
} 