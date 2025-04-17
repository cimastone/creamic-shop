package com.ceramic.user.interfaces.rest;

import com.ceramic.user.application.dto.AddressDTO;
import com.ceramic.user.application.dto.AddressRequest;
import com.ceramic.user.application.service.AddressApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

/**
 * 地址控制器 - 处理用户收货地址相关的API请求
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressApplicationService addressApplicationService;

    @Autowired
    public AddressController(AddressApplicationService addressApplicationService) {
        this.addressApplicationService = addressApplicationService;
    }

    /**
     * 添加收货地址
     */
    @PostMapping("")
    public ResponseEntity<AddressDTO> addAddress(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddressRequest request) {
        try {
            AddressDTO addressDTO = addressApplicationService.addAddress(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(addressDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        try {
            AddressDTO addressDTO = addressApplicationService.updateAddress(userId, addressId, request);
            return ResponseEntity.ok(addressDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 设置默认地址
     */
    @PostMapping("/{addressId}/default")
    public ResponseEntity<AddressDTO> setDefaultAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId) {
        try {
            AddressDTO addressDTO = addressApplicationService.setDefaultAddress(userId, addressId);
            return ResponseEntity.ok(addressDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId) {
        try {
            addressApplicationService.deleteAddress(userId, addressId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 获取用户的所有收货地址
     */
    @GetMapping("")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(
            @RequestHeader("X-User-Id") Long userId) {
        List<AddressDTO> addresses = addressApplicationService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    /**
     * 获取地址详情
     */
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId) {
        try {
            AddressDTO addressDTO = addressApplicationService.getAddressById(userId, addressId);
            return ResponseEntity.ok(addressDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 获取用户的默认收货地址
     */
    @GetMapping("/default")
    public ResponseEntity<AddressDTO> getUserDefaultAddress(
            @RequestHeader("X-User-Id") Long userId) {
        return addressApplicationService.getUserDefaultAddress(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Default address not found"));
    }
} 