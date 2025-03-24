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
        
        // 转换地址信息
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setReceiverName(request.getReceiverName());
        addressDTO.setReceiverPhone(request.getReceiverPhone());
        addressDTO.setReceiverAddress(request.getReceiverAddress());
        
        // 创建订单
        OrderDTO orderDTO = orderApplicationService.createOrder(userId, addressDTO, request.getItems());
        
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
        // 在实际项目中，这里应该解析JWT token并提取用户ID
        // 这里简化处理，假设token格式为"Bearer userId"
        return Long.parseLong(token.substring(7));
    }
} 