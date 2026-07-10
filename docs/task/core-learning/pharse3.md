### 3\. Student - Complete Lesson (Hoàn thành bài học)

**Mô tả:** Tính năng này được kích hoạt khi Frontend phát hiện video đã chạy đến những giây cuối cùng (hoặc học viên chủ động bấm nút "Đánh dấu hoàn thành" đối với dạng bài đọc Document).

**Ý tưởng thiết kế:**

Đây là nơi tác động mạnh nhất đến bảng LearningProgress (tiến độ tổng). Cần dùng cơ chế Transaction chặt chẽ vì ta phải update 2 bảng cùng lúc.

**Logic nghiệp vụ (Business Logic):**

*   **Input:** studentId, courseId, lessonId.
    
*   **Step 1:** Tìm LessonProgress. Check Idempotency: Nếu bài này đã COMPLETE rồi thì bỏ qua, return 200 luôn để không cộng dồn sai % tiến độ.
    
*   **Step 2:** Chuyển status của bài học thành COMPLETE, ghi nhận completed\_at = NOW().
    
*   **Step 3:** Mở bảng LearningProgress lên:
    
    *   Tăng cột completed\_lessons lên 1.
        
    *   Tính toán lại: progress\_percent = (completed\_lessons / total\_lessons) \* 100.
        
*   **Step 4 (Hook kích nổ):** Check ngay tại đây, nếu completed\_lessons == total\_lessons (nghĩa là đã học xong 100% các bài), thì ngầm gọi luôn hàm xử lý của tính năng số 4 (Complete Course).
    

**Đặc tả API:**

**MethodEndpointBody/Payload**POST/api/learning/courses/{courseId}/lessons/{lessonId}/completeTrống (Empty Body)