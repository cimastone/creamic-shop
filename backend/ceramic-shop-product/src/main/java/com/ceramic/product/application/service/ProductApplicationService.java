package com.ceramic.product.application.service;

import com.ceramic.product.application.dto.ProductDTO;
import com.ceramic.product.domain.model.aggregate.Product;
import com.ceramic.product.domain.model.valueobject.*;
import com.ceramic.product.domain.repository.ProductRepository;
import com.ceramic.product.domain.service.ProductDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 产品应用服务
 */
@Service
public class ProductApplicationService {
    
    private final ProductRepository productRepository;
    private final ProductDomainService productDomainService;
    
    public ProductApplicationService(ProductRepository productRepository, ProductDomainService productDomainService) {
        this.productRepository = productRepository;
        this.productDomainService = productDomainService;
    }
    
    /**
     * 查询所有产品
     */
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID查询产品
     */
    public ProductDTO getProductById(Long id) {
        Optional<Product> product = productRepository.findById(new ProductId(id));
        return product.map(this::toDTO).orElse(null);
    }
    
    /**
     * 根据分类查询产品
     */
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(new Category(category))
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据名称模糊查询产品
     */
    public List<ProductDTO> getProductsByNameLike(String namePattern) {
        return productRepository.findByNameLike(namePattern)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 创建产品
     */
    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = Product.builder()
                .name(new ProductName(dto.getName()))
                .description(dto.getDescription() != null ? new ProductDescription(dto.getDescription()) : null)
                .price(new Money(dto.getPrice()))
                .category(new Category(dto.getCategory()))
                .image(dto.getImage() != null ? new ImageUrl(dto.getImage()) : null)
                .inventory(new Inventory(dto.getStock()))
                .status(ProductStatus.valueOf(dto.getStatus() != null ? dto.getStatus() : "OFFLINE"))
                .build();
        
        Product savedProduct = productRepository.save(product);
        return toDTO(savedProduct);
    }
    
    /**
     * 更新产品
     */
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Optional<Product> optionalProduct = productRepository.findById(new ProductId(id));
        if (!optionalProduct.isPresent()) {
            return null;
        }
        
        Product product = optionalProduct.get();
        product.update(
            new ProductName(dto.getName()),
            dto.getDescription() != null ? new ProductDescription(dto.getDescription()) : null,
            new Money(dto.getPrice()),
            new Category(dto.getCategory()),
            dto.getImage() != null ? new ImageUrl(dto.getImage()) : null
        );
        
        // 如果修改了库存
        if (dto.getStock() != null && product.getInventory().getAvailableQuantity() != dto.getStock()) {
            product.updateInventory(new Inventory(dto.getStock()));
        }
        
        // 如果修改了状态
        if (dto.getStatus() != null && !product.getStatus().name().equals(dto.getStatus())) {
            if (ProductStatus.ONLINE.name().equals(dto.getStatus())) {
                product.online();
            } else if (ProductStatus.OFFLINE.name().equals(dto.getStatus())) {
                product.offline();
            }
        }
        
        Product updatedProduct = productRepository.save(product);
        return toDTO(updatedProduct);
    }
    
    /**
     * 删除产品
     */
    @Transactional
    public boolean deleteProduct(Long id) {
        return productRepository.remove(new ProductId(id));
    }
    
    /**
     * 上架产品
     */
    @Transactional
    public boolean onlineProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(new ProductId(id));
        if (!optionalProduct.isPresent()) {
            return false;
        }
        
        try {
            Product product = optionalProduct.get();
            product.online();
            productRepository.save(product);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
    
    /**
     * 下架产品
     */
    @Transactional
    public boolean offlineProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(new ProductId(id));
        if (!optionalProduct.isPresent()) {
            return false;
        }
        
        Product product = optionalProduct.get();
        product.offline();
        productRepository.save(product);
        return true;
    }
    
    /**
     * 检查并扣减库存
     */
    @Transactional
    public boolean checkAndDecreaseInventory(Long productId, int quantity) {
        return productDomainService.checkAndDecreaseInventory(new ProductId(productId), quantity);
    }
    
    /**
     * 获取所有分类
     */
    public List<String> getAllCategories() {
        return productRepository.findAllCategories()
                .stream()
                .map(Category::getValue)
                .collect(Collectors.toList());
    }
    
    /**
     * 将领域对象转换为DTO
     */
    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId().getValue());
        dto.setName(product.getName().getValue());
        if (product.getDescription() != null) {
            dto.setDescription(product.getDescription().getValue());
        }
        dto.setPrice(product.getPrice().getAmount());
        dto.setCategory(product.getCategory().getValue());
        if (product.getImage() != null) {
            dto.setImage(product.getImage().getValue());
        }
        dto.setStock(product.getInventory().getAvailableQuantity());
        dto.setStatus(product.getStatus().name());
        dto.setCreateTime(product.getCreateTime());
        dto.setUpdateTime(product.getUpdateTime());
        return dto;
    }
} 