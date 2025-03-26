package com.ceramicshop.user.interfaces.rest;

import com.ceramicshop.user.application.ShippingAddressService;
import com.ceramicshop.user.application.dto.ShippingAddressDTO;
import com.ceramicshop.user.interfaces.rest.request.CreateAddressRequest;
import com.ceramicshop.user.interfaces.rest.request.UpdateAddressRequest;
import com.ceramicshop.user.interfaces.rest.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 */
@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ShippingAddressController {

    private final ShippingAddressService addressService;

    @Autowired
    public ShippingAddressController(ShippingAddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * 创建收货地址
     */
    @PostMapping
    public ApiResponse<ShippingAddressDTO> createAddress(@RequestBody CreateAddressRequest request,
                                                       @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        
        ShippingAddressDTO addressDTO = ShippingAddressDTO.builder()
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .province(request.getProvince())
                .city(request.getCity())
                .district(request.getDistrict())
                .detailAddress(request.getDetailAddress())
                .isDefault(request.isDefault())
                .build();
        
        ShippingAddressDTO createdAddress = addressService.createAddress(userId, addressDTO);
        return ApiResponse.success(createdAddress);
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/{id}")
    public ApiResponse<ShippingAddressDTO> updateAddress(@PathVariable Long id,
                                                       @RequestBody UpdateAddressRequest request,
                                                       @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        
        // 验证地址所属用户
        ShippingAddressDTO existingAddress = addressService.getAddressById(id);
        if (!existingAddress.getUserId().equals(userId)) {
            return ApiResponse.error("无权限修改该地址");
        }
        
        ShippingAddressDTO addressDTO = ShippingAddressDTO.builder()
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .province(request.getProvince())
                .city(request.getCity())
                .district(request.getDistrict())
                .detailAddress(request.getDetailAddress())
                .isDefault(request.isDefault())
                .build();
        
        ShippingAddressDTO updatedAddress = addressService.updateAddress(id, addressDTO);
        return ApiResponse.success(updatedAddress);
    }

    /**
     * 获取收货地址列表
     */
    @GetMapping
    public ApiResponse<List<ShippingAddressDTO>> getAddressList(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        List<ShippingAddressDTO> addresses = addressService.getAddressesByUserId(userId);
        return ApiResponse.success(addresses);
    }

    /**
     * 获取收货地址详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ShippingAddressDTO> getAddressDetail(@PathVariable Long id,
                                                          @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        
        ShippingAddressDTO address = addressService.getAddressById(id);
        if (!address.getUserId().equals(userId)) {
            return ApiResponse.error("无权限查看该地址");
        }
        
        return ApiResponse.success(address);
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long id,
                                         @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        
        // 验证地址所属用户
        ShippingAddressDTO existingAddress = addressService.getAddressById(id);
        if (!existingAddress.getUserId().equals(userId)) {
            return ApiResponse.error("无权限删除该地址");
        }
        
        addressService.deleteAddress(id);
        return ApiResponse.success();
    }

    /**
     * 设置默认收货地址
     */
    @PostMapping("/{id}/default")
    public ApiResponse<Void> setDefaultAddress(@PathVariable Long id,
                                             @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        
        // 验证地址所属用户
        ShippingAddressDTO existingAddress = addressService.getAddressById(id);
        if (!existingAddress.getUserId().equals(userId)) {
            return ApiResponse.error("无权限设置该地址");
        }
        
        addressService.setDefaultAddress(id, userId);
        return ApiResponse.success();
    }

    /**
     * 获取默认收货地址
     */
    @GetMapping("/default")
    public ApiResponse<ShippingAddressDTO> getDefaultAddress(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        ShippingAddressDTO defaultAddress = addressService.getDefaultAddress(userId);
        if (defaultAddress == null) {
            return ApiResponse.error("未设置默认地址");
        }
        return ApiResponse.success(defaultAddress);
    }

    /**
     * 从JWT token中提取用户ID
     */
    private Long extractUserIdFromToken(String token) {
        // 在实际项目中，这里应该解析JWT token并提取用户ID
        // 这里简化处理，假设token格式为"Bearer mock-token-for-user-123"或"mock-token-for-user-123"
        String tokenValue = token;
        if (token.startsWith("Bearer ")) {
            tokenValue = token.substring(7);
        }
        
        // 提取用户ID部分
        String userIdPart = tokenValue;
        if (tokenValue.contains("mock-token-for-user-")) {
            userIdPart = tokenValue.replace("mock-token-for-user-", "");
        }
        
        try {
            return Long.parseLong(userIdPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的token格式: " + token, e);
        }
    }
} 