package com.ceramicshop.order.interfaces.rest;

import com.ceramicshop.order.application.ShippingAddressService;
import com.ceramicshop.order.application.dto.ShippingAddressDTO;
import com.ceramicshop.order.interfaces.rest.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 物流地址控制器
 */
@RestController
@RequestMapping("/api/shipping-addresses")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ShippingAddressController {

    private final ShippingAddressService addressService;

    @Autowired
    public ShippingAddressController(ShippingAddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * 获取订单的物流地址
     */
    @GetMapping("/order/{orderId}")
    public ApiResponse<ShippingAddressDTO> getAddressByOrderId(@PathVariable Long orderId) {
        Optional<ShippingAddressDTO> addressOpt = addressService.getAddressByOrderId(orderId);
        return addressOpt.map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error("订单物流地址不存在"));
    }

    /**
     * 获取物流地址详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ShippingAddressDTO> getAddressDetail(@PathVariable Long id) {
        try {
            ShippingAddressDTO address = addressService.getAddressById(id);
            return ApiResponse.success(address);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新物流地址
     */
    @PutMapping("/{id}")
    public ApiResponse<ShippingAddressDTO> updateAddress(@PathVariable Long id,
                                                      @RequestBody ShippingAddressDTO addressDTO) {
        try {
            ShippingAddressDTO updatedAddress = addressService.updateAddress(id, addressDTO);
            return ApiResponse.success(updatedAddress);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
} 