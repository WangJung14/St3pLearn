---
title: "[Bug] API Gateway chặn & thiếu route cho các endpoint Categories, Tags và lỗi 503 Enrollments"
labels: ["bug", "backend", "api-gateway"]
assignees: []
---

## 🐛 Mô tả lỗi (Bug Description)
Hiện tại Frontend gặp 3 lỗi khi gọi API qua `api-gateway`:
1. `GET /api/tags`: Trả về `404 Not Found`.
2. `GET /api/categories`: Trả về `401 Unauthorized` mặc dù đây là public API (hoặc FE không kẹp token).
3. `GET /api/enrollments/my-courses`: Trả về `503 Service Unavailable`.

## 🔍 Phân tích nguyên nhân (Root Cause)
1. **Lỗi 404 (`/api/tags`)**: Trong `application.yaml` của `api-gateway`, predicate `catalog-service-route` đang thiếu path `/api/tags/**`. Do đó Gateway không biết chuyển hướng đi đâu và báo 404.
2. **Lỗi 401 (`/api/categories`)**: Trong `AuthenticationFilter.java` của `api-gateway`, danh sách `publicEndpoints` không bao gồm `/api/categories` và `/api/tags`. Gateway đang block mọi request không có JWT Token, mặc dù `TagController` và `CategoryController` (GET methods) được thiết kế cho Public Access.
3. **Lỗi 503 (`learning-service`)**: Gateway không tìm thấy instance của `learning-service`. Thường do Eureka chưa kịp đồng bộ (cần đợi 30-60s sau khi service khởi động) hoặc `learning-service` thực sự đang bị tắt/crash.

## 🛠 Cách khắc phục đề xuất (Proposed Fix)

### 1. Cập nhật `application.yaml` (api-gateway)
Thêm `/api/tags/**` vào danh sách Path của `catalog-service-route`:
```yaml
        - id: catalog-service-route
          uri: lb://catalog-service
          predicates:
            - Path=/api/catalog/**, /api/courses/**, /api/categories/**, /api/wishlists/**, /api/tags/**
```

### 2. Cập nhật `AuthenticationFilter.java` (api-gateway)
Chỉnh sửa logic lọc để cho phép method `GET` đi qua mà không cần token đối với Categories và Tags:
```java
        // 2.Public gateway
        boolean isPublic = publicEndpoints.stream().anyMatch(path::startsWith);
        
        // Bổ sung: Cho phép GET categories và tags không cần token
        if (!isPublic && HttpMethod.GET.equals(method)) {
            if (path.startsWith("/api/categories") || path.startsWith("/api/tags")) {
                isPublic = true;
            }
        }

        if (isPublic) {
            return chain.filter(exchange);
        }
```

### 3. Đảm bảo `learning-service` hoạt động
- Kiểm tra lại console của `learning-service`. Đợi cho tới khi dòng `Registered instance ... with eureka` xuất hiện.

## 📋 Môi trường tái hiện (Environment)
- Local Development
- API Gateway (Port: 8080)
- FE Admin Dashboard (`http://localhost:3000/dashboard/admin/tags`)
