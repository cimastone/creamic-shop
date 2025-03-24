package com.ceramicshop.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    
    // 客户端错误
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    
    // 业务错误
    ORDER_NOT_FOUND(1001, "订单不存在"),
    PRODUCT_NOT_FOUND(1002, "商品不存在"),
    STOCK_NOT_ENOUGH(1003, "库存不足"),
    PAYMENT_FAILED(1004, "支付失败"),
    USER_NOT_FOUND(1005, "用户不存在"),
    
    // 服务器错误
    SYSTEM_ERROR(500, "系统错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用");

    private final int code;
    private final String message;
} 