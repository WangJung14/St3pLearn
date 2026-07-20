3\. Teacher - View Exam Submissions (Danh sách bài thi chờ chấm)
----------------------------------------------------------------

**Mô tả:** Bảng Dashboard để giáo viên vào xem lớp mình có bao nhiêu đứa đã nộp bài, ai đang chờ chấm điểm.

**Logic nghiệp vụ (Business Logic):**

*   **Input:** instructorId (từ Token), examId, status (tùy chọn: NEEDS\_GRADING, COMPLETED), thông số phân trang page, size.
    
*   **Step 1 - Security:** Join bảng để check examId đó có thực sự do instructorId này tạo ra không.
    
*   **Step 2 - Query:** Lấy danh sách ExamSubmission, join với bảng users/students để lấy Tên và Email học viên hiển thị cho đẹp. Trả về dưới dạng Page.
    

**Đặc tả API:**

**MethodEndpointNhiệm vụGET**/api/learning/exams/{examId}/submissions?status=NEEDS\_GRADING&page=0Phân trang danh sách nộp bài