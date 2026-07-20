Nền tảng Database: Bảng certificates
------------------------------------

Trước khi đi vào API, Cần phải đọc DB design để thiết kế table cho certification


1\. Teacher - Issue Certificate (Cấp phát thủ công)
---------------------------------------------------

**Mô tả:** Trong một hệ thống lý tưởng (như định hướng ở Phase 1), khi bài học cuối cùng hoàn thành, hệ thống sẽ bắn event qua RabbitMQ để tự cấp chứng chỉ. Tuy nhiên, luôn cần một API cấp tay (Manual) cho Giáo viên hoặc Admin để đề phòng event bị rớt mạng, hoặc học viên yêu cầu cấp lại.

**Logic nghiệp vụ (Business Logic):**

*   **Input:** studentId, courseId, instructorId (từ Token).
    
*   **Step 1 - Validate Quyền hạn:** Kiểm tra xem instructorId có đúng là chủ nhân của courseId này không.
    
*   **Step 2 - Validate Điều kiện tốt nghiệp:** Truy vấn bảng LearningProgress xem progress\_percent đã đạt 100% chưa. Truy vấn bảng ExamSubmission (nếu khóa học có thi) xem đã pass chưa. Nếu chưa đủ điều kiện -> Ném lỗi REQUIREMENTS\_NOT\_MET.
    
*   **Step 3 - Check Idempotency:** Kiểm tra trong bảng certificates xem cặp studentId - courseId này đã có chứng chỉ chưa. Nếu có rồi, trả về thông tin chứng chỉ cũ, không tạo mới (tránh 1 khóa học đẻ ra 10 cái chứng chỉ).
    
*   **Step 4 - Sinh Code độc nhất:** Tạo một chuỗi certificate\_code ngẫu nhiên nhưng phải Unique (có thể kết hợp tiền tố + UUID rút gọn + năm).
    
*   **Step 5:** Lưu vào Database.
    

**Đặc tả API:**

**MethodEndpointPayload mẫuPOST**/api/learning/certificates/issue{ "studentId": "uuid", "courseId": "uuid" }