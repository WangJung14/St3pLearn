package com.tommy.common.security;

import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class RoleAspect {

    // Kích hoạt trước khi chạy vào bất kỳ hàm nào có gắn @RequireRole
    @Before("@annotation(requireRole)")
    public void checkRole(RequireRole requireRole) {

        // 1. Tự động lấy Request hiện tại ra
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // 2. Móc Header từ API Gateway truyền xuống
        String userRole = request.getHeader("X-User-Role");

        // 3. Kiểm tra xem quyền của User có nằm trong danh sách được phép không
        List<String> allowedRoles = Arrays.asList(requireRole.value());

        if (userRole == null || !allowedRoles.contains(userRole.toUpperCase())) {
            log.warn("Access Denied: User role '{}' is not in allowed roles {}", userRole, allowedRoles);
            throw new AppException(ErrorCode.FORBIDDEN_ROLE); // Tự động văng lỗi 403
        }
    }
}