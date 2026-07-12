### 4\. Student - Complete Course (Tốt nghiệp khóa học)

**Mô tả:** Khóa học kết thúc, trạng thái enrollment chuyển sang hoàn thành, hệ thống bắn pháo hoa ăn mừng và thông báo cho các Service khác biết.

**Ý tưởng thiết kế:**Tính năng này thường **không phải là 1 API phơi ra cho Frontend gọi** (để tránh bị hacker gọi thẳng API này fake kết quả tốt nghiệp). Nó nên là một hàm _private_ hoặc _internal_ được trigger tự động từ Tính năng số 3 khi đạt 100%.

**Logic nghiệp vụ (Business Logic):**

*   **Input:** Gọi ngầm từ hàm Complete Lesson, truyền vào enrollmentId.
    
*   **Step 1:** Kiểm tra lại lần cuối xem progress\_percent đã thực sự chạm 100% chưa.
    
*   **Step 2:** Đổi status của Enrollment từ ACTIVE sang COMPLETED. Lưu completed\_at = NOW().
    
*   **Step 3 (Rất quan trọng cho Phase 4):** Đóng gói một bưu kiện RabbitMQ mang tên CourseCompletedEvent (chứa studentId, courseId) và ném lên Exchange. Sau này con service Certificate sẽ vểnh tai bắt cái event này để tự động in chứng chỉ.