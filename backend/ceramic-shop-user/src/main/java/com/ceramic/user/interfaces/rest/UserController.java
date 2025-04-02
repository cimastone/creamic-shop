package com.ceramic.user.interfaces.rest;

import com.ceramic.user.application.dto.*;
import com.ceramic.user.application.service.UserApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserApplicationService userApplicationService;

    @Autowired
    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterUserRequest request) {
        try {
            UserDTO userDTO = userApplicationService.register(request);
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        return userApplicationService.login(request)
                .map(userDTO -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("user", userDTO);
                    // 这里可以添加JWT token等认证信息
                    response.put("token", "mock-token-for-user-" + userDTO.getId());
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        // 通过Spring Security获取当前用户名，这里简化处理
        // 实际应该从SecurityContextHolder获取Authentication
        String username = "cimastone"; // 临时硬编码，仅供演示
        
        return userApplicationService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        return userApplicationService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateProfile(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userApplicationService.updateProfile(userId, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<UserDTO> changePassword(
            @PathVariable Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            UserDTO userDTO = userApplicationService.changePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 添加收货地址
     */
    @PostMapping("/{userId}/addresses")
    public ResponseEntity<AddressDTO> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressRequest request) {
        try {
            AddressDTO addressDTO = userApplicationService.addAddress(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(addressDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        try {
            AddressDTO addressDTO = userApplicationService.updateAddress(userId, addressId, request);
            return ResponseEntity.ok(addressDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/{userId}/addresses/{addressId}/default")
    public ResponseEntity<AddressDTO> setDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        try {
            AddressDTO addressDTO = userApplicationService.setDefaultAddress(userId, addressId);
            return ResponseEntity.ok(addressDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        try {
            userApplicationService.deleteAddress(userId, addressId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 获取用户的所有收货地址
     */
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(@PathVariable Long userId) {
        List<AddressDTO> addresses = userApplicationService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    /**
     * 获取用户的默认收货地址
     */
    @GetMapping("/{userId}/addresses/default")
    public ResponseEntity<AddressDTO> getUserDefaultAddress(@PathVariable Long userId) {
        return userApplicationService.getUserDefaultAddress(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Default address not found"));
    }
} 