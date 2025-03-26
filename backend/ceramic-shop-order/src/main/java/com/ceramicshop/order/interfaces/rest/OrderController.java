package com.ceramicshop.order.interfaces.rest;

import com.ceramicshop.order.application.OrderApplicationService;
import com.ceramicshop.order.application.dto.AddressDTO;
import com.ceramicshop.order.application.dto.OrderDTO;
import com.ceramicshop.order.application.dto.OrderItemDTO;
import com.ceramicshop.order.domain.model.OrderStatus;
import com.ceramicshop.order.interfaces.rest.request.CreateOrderRequest;
import com.ceramicshop.order.interfaces.rest.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    
    private final OrderApplicationService orderApplicationService;
    
    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }
    
    /**
     * 创建订单
     */
    @PostMapping
    public ApiResponse<OrderDTO> createOrder(@RequestBody CreateOrderRequest request, @RequestHeader("Authorization") String token) {
        // 提取用户ID
        Long userId = extractUserIdFromToken(token);
        
        // 调试日志
        System.out.println("-----创建订单请求-----");
        System.out.println("用户ID: " + userId);
        System.out.println("地址ID: " + request.getAddressId());
        System.out.println("收货人: " + request.getReceiverName());
        System.out.println("收货电话: " + request.getReceiverPhone());
        System.out.println("收货地址: " + request.getReceiverAddress());
        System.out.println("订单项数量: " + (request.getItems() != null ? request.getItems().size() : "null"));
        
        // 请求验证
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("订单项不能为空");
        }
        
        OrderDTO orderDTO;
        
        // 判断使用哪种方式创建订单
        if (request.hasReceiverInfo()) {
            // 方式2：直接使用收货人信息创建订单
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setReceiverName(request.getReceiverName());
            addressDTO.setReceiverPhone(request.getReceiverPhone());
            addressDTO.setReceiverAddress(request.getReceiverAddress());
            
            orderDTO = orderApplicationService.createOrder(userId, addressDTO, request.getItems());
        } else if (request.getAddressId() != null) {
            // 方式1：通过地址ID创建订单
            orderDTO = orderApplicationService.createOrder(userId, request.getAddressId(), request.getItems());
        } else {
            throw new IllegalArgumentException("收货地址信息不完整，请提供地址ID或完整的收货人信息");
        }
        
        return ApiResponse.success(orderDTO);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderDTO> getOrderDetail(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        // 提取用户ID
        Long userId = extractUserIdFromToken(token);
        
        // 获取订单详情
        OrderDTO orderDTO = orderApplicationService.getOrderById(id, userId);
        
        return ApiResponse.success(orderDTO);
    }
    
    /**
     * 获取用户所有订单
     */
    @GetMapping
    public ApiResponse<List<OrderDTO>> getUserOrders(@RequestHeader("Authorization") String token) {
        // 提取用户ID
        Long userId = extractUserIdFromToken(token);
        
        // 获取用户所有订单
        List<OrderDTO> orders = orderApplicationService.getUserOrders(userId);
        
        return ApiResponse.success(orders);
    }
    
    /**
     * 获取用户特定状态的订单
     */
    @GetMapping("/status/{status}")
    public ApiResponse<List<OrderDTO>> getUserOrdersByStatus(@PathVariable String status, @RequestHeader("Authorization") String token) {
        // 提取用户ID
        Long userId = extractUserIdFromToken(token);
        
        // 转换状态
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        
        // 获取用户特定状态的订单
        List<OrderDTO> orders = orderApplicationService.getUserOrdersByStatus(userId, orderStatus);
        
        return ApiResponse.success(orders);
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/{id}/pay")
    public ApiResponse<OrderDTO> payOrder(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        // 提取用户ID
        Long userId = extractUserIdFromToken(token);
        
        // 支付订单
        OrderDTO orderDTO = orderApplicationService.payOrder(id, userId);
        
        return ApiResponse.success(orderDTO);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        // 提取用户ID
        Long userId = extractUserIdFromToken(token);
        
        // 取消订单
        OrderDTO orderDTO = orderApplicationService.cancelOrder(id, userId);
        
        return ApiResponse.success(orderDTO);
    }
    
    /**
     * 发货订单（管理员接口）
     */
    @PostMapping("/{id}/ship")
    public ApiResponse<OrderDTO> shipOrder(@PathVariable Long id) {
        // 发货订单
        OrderDTO orderDTO = orderApplicationService.shipOrder(id);
        
        return ApiResponse.success(orderDTO);
    }
    
    /**
     * 完成订单
     */
    @PostMapping("/{id}/complete")
    public ApiResponse<OrderDTO> completeOrder(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        // 提取用户ID
        Long userId = extractUserIdFromToken(token);
        
        // 完成订单
        OrderDTO orderDTO = orderApplicationService.completeOrder(id, userId);
        
        return ApiResponse.success(orderDTO);
    }
    
    /**
     * 从JWT token中提取用户ID
     */
    private Long extractUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token不能为空");
        }

        // 调试日志
        System.out.println("正在验证token: " + token);

        // 移除Bearer前缀
        String tokenValue = token;
        if (token.startsWith("Bearer ")) {
            tokenValue = token.substring(7);
        }

        try {
            // 在实际项目中，这里应该使用JWT库验证token
            // 这里简化处理，假设token格式为"mock-token-for-user-123"
            if (!tokenValue.startsWith("mock-token-for-user-")) {
                throw new IllegalArgumentException("无效的token格式");
            }

            String userIdStr = tokenValue.replace("mock-token-for-user-", "");
            Long userId = Long.parseLong(userIdStr);

            // 验证用户ID是否有效
            if (userId <= 0) {
                throw new IllegalArgumentException("无效的用户ID");
            }

            System.out.println("成功提取用户ID: " + userId);
            return userId;

        } catch (NumberFormatException e) {
            System.err.println("Token解析失败: " + e.getMessage());
            throw new IllegalArgumentException("无效的token格式: " + token, e);
        } catch (Exception e) {
            System.err.println("Token验证失败: " + e.getMessage());
            throw new IllegalArgumentException("Token验证失败: " + e.getMessage(), e);
        }
    }
} 