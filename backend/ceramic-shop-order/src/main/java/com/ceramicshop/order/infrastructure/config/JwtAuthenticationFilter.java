package com.ceramicshop.order.infrastructure.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器
 * 注意：这只是一个简化版实现，实际项目中应该验证JWT的签名和有效期
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 从请求头中获取token
        String token = getTokenFromRequest(request);
        
        // 如果token存在且不在白名单URL中，则进行验证
        if (StringUtils.hasText(token)) {
            try {
                // 解析token（实际项目中应该使用JWT库解析并验证）
                // 这里简化处理，假设token中包含用户ID
                Long userId = parseUserIdFromToken(token);
                
                // 创建认证对象
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                
                // 设置认证信息到上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                logger.error("JWT认证失败", e);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求头中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * 从token中解析用户ID
     * 注意：这是一个简化实现，实际项目应该使用JWT库
     */
    private Long parseUserIdFromToken(String token) {
        // 实际项目应该使用JWT库解析token，此处为了简单，假设token直接是用户ID
        try {
            return Long.parseLong(token);
        } catch (NumberFormatException e) {
            // 如果不是数字，返回默认值
            return 0L;
        }
    }
} 