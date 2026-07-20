Nền tảng Database: Bảng certificates
------------------------------------

Trước khi đi vào API, em cần thiết kế bảng này với các trường cốt lõi:

*   id: UUID (Khóa chính hệ thống).
    
*   certificate\_code: String (Khóa phụ duy nhất - Unique, ví dụ: CERT-UTH-9A8B7C, dùng để verify).
    
*   student\_id, course\_id: UUID (Liên kết người học và khóa học).
    
*   issue\_date: LocalDateTime (Ngày cấp).
    
*   pdf\_url: String (Đường dẫn tải file nếu em lưu lên S3, hoặc bỏ qua nếu em chọn render PDF on-the-fly).
    
*   is\_revoked: Boolean (Cờ thu hồi chứng chỉ nếu phát hiện gian lận, mặc định là false).
    

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