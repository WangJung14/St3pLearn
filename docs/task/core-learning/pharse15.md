3\. Student/Public - Verify Certificate (Tra cứu chứng chỉ)
-----------------------------------------------------------

**Mô tả:** Đây là API "nở mày nở mặt" nhất. Học viên copy cái mã số (ví dụ: CERT-2026-ABC) dán lên LinkedIn. Nhà tuyển dụng bấm vào link, hệ thống sẽ hiện ra: "Chứng chỉ này là Thật, cấp cho anh Nguyễn Văn A, khóa học Java Spring Boot, ngày 20/07/2026".

**Ý tưởng thiết kế:**

Đây phải là một **Public API** (không yêu cầu JWT Token ở header), bất kỳ ai có mã code đều tra cứu được.

**Logic nghiệp vụ (Business Logic):**

*   **Input:** certificateCode (truyền qua URL).
    
*   **Step 1:** Query Database bảng certificates với điều kiện certificate\_code = ?.
    
*   **Step 2:**
    
    *   Nếu không tìm thấy -> Trả về lỗi 404 CERTIFICATE\_NOT\_FOUND (Báo động chứng chỉ giả).
        
    *   Nếu tìm thấy nhưng is\_revoked = true -> Trả về trạng thái "Chứng chỉ đã bị thu hồi".
        
    *   Nếu tìm thấy và hợp lệ -> Lấy thêm thông tin Tên học viên, Tên khóa học, Tên giáo viên ký.
        
*   **Step 3:** Trả về cục JSON để Frontend vẽ một cái thẻ (Card) xác nhận xanh lá cây rực rỡ.
    

**Đặc tả API:**

**MethodEndpointResponse quan trọngGET**/api/learning/certificates/verify/{certificateCode}{ "isValid": true, "studentName": "Tommy", "courseName": "Microservices", ... }