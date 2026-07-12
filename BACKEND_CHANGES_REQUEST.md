# Yêu cầu cập nhật cấu hình Backend

Trong quá trình debug hệ thống, chúng ta đã phát hiện và xử lý một số vấn đề liên quan đến cấu hình bảo mật và RabbitMQ ở các service. Hiện tại các thay đổi này đã được rollback theo yêu cầu, nhưng cần được apply lại để hệ thống hoạt động ổn định.

Dưới đây là danh sách các thay đổi cần thực hiện trên Backend:

## 1. api-gateway
**File**: `api-gateway/src/main/java/com/tommy/gateway/infrastructure/security/AuthenticationFilter.java`
**Vấn đề**: API public `/api/categories` đang bị chặn trả về 401 Unauthorized cho user chưa đăng nhập.
**Giải pháp**: Thêm `"/api/categories"` vào danh sách `publicEndpoints`.

```java
    private final List<String> publicEndpoints = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/verify-email",
            "/api/catalog/p/",
            "/api/courses/p/",
            "/api/courses/bulk-summaries",
            "/api/categories" // Thêm endpoint này
    );
```

## 2. identity-service
**File**: `identity-service/src/main/java/com/tommy/identity/infrastructure/config/RabbitMQConfig.java`
**Vấn đề**: Thiếu bean `RabbitTemplate`, dẫn đến lỗi 500 khi bắn event sau khi xác thực.
**Giải pháp**: Thêm bean `RabbitTemplate`.

```java
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
```

## 3. learning-service
**Vấn đề**: Service không start được do lỗi trùng lặp Bean `RabbitMQConfig` (có 2 file cấu hình RabbitMQ trong service này).
**Giải pháp**: Gộp 2 file cấu hình lại làm 1.

**Bước 3.1:** Xóa file cấu hình cũ
Xóa file: `learning-service/src/main/java/com/tommy/learning/config/RabbitMQConfig.java`

**Bước 3.2:** Cập nhật file cấu hình chính
File: `learning-service/src/main/java/com/tommy/learning/infrastructure/messaging/RabbitMQConfig.java`
Thêm cấu hình exchange cho `course.completed` và `RabbitTemplate`:

```java
    // Thêm hằng số
    public static final String COURSE_COMPLETED_EXCHANGE = "course.completed.exchange";

    // Thêm các bean
    @Bean
    public FanoutExchange courseCompletedExchange() {
        return new FanoutExchange(COURSE_COMPLETED_EXCHANGE);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
```

**Bước 3.3:** Cập nhật import trong Listener
File: `learning-service/src/main/java/com/tommy/learning/application/listener/CourseCompletedEventListener.java`
Đổi import `RabbitMQConfig`:
```java
// Từ:
import com.tommy.learning.config.RabbitMQConfig;
// Thành:
import com.tommy.learning.infrastructure.messaging.RabbitMQConfig;
```
