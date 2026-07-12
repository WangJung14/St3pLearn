1\. Teacher - Create / Update / Delete Question Bank
----------------------------------------------------

**Mô tả:** Ngân hàng câu hỏi (Question Bank) giống như các thư mục (Folder) phân loại theo chủ đề, độ khó hoặc chương trình học. Ví dụ: "Ngân hàng IELTS Reading", "Luyện tập từ vựng A1".

**Ý tưởng thiết kế:**

Entity này khá đơn giản, đóng vai trò là container. Tuy nhiên, tính năng **Delete** là cái bẫy lớn nhất. Tuyệt đối không được dùng lệnh DELETE vật lý dưới Database, mà phải dùng cơ chế **Soft Delete** (Xóa mềm) để bảo toàn dữ liệu lịch sử.

**Logic nghiệp vụ (Business Logic):**

*   **Create:**
    
    *   Input: title, description.
        
    *   Hệ thống tự động lấy instructorId từ Token và gán vào ngân hàng.
        
*   **Update:**
    
    *   Input: bankId, title, description.
        
    *   _Bảo mật:_ Bắt buộc kiểm tra instructorId trong Token có khớp với instructorId của bankId đó dưới DB không. Không được phép sửa ngân hàng của giáo viên khác.
        
*   **Delete (Soft Delete):**
    
    *   Input: bankId.
        
    *   Chuyển cờ is\_deleted = true. Mọi câu query lấy danh sách sau này phải thêm điều kiện WHERE is\_deleted = false.
        

**Đặc tả API:**

**MethodEndpointNhiệm vụPOST**/api/learning/question-banksTạo ngân hàng mới**PUT**/api/learning/question-banks/{bankId}Cập nhật thông tin**DELETE**/api/learning/question-banks/{bankId}Xóa mềm ngân hàng