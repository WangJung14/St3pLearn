### 2\. Student - Resume Learning (Tiếp tục học)

**Mô tả:** Học viên đang học dở hôm qua, hôm nay quay lại bấm nút "Tiếp tục học". Hệ thống phải trả về chính xác ID bài học hôm qua và số giây họ dừng lại để Frontend phát tiếp video.

**Ý tưởng thiết kế:**

Tận dụng lại cột last\_accessed\_lesson\_id ở bảng Enrollment mà tính năng số 1 vừa lưu vết.

**Logic nghiệp vụ (Business Logic):**

*   **Input:** studentId, courseId.
    
*   **Step 1:** Tìm Enrollment hợp lệ.
    
*   **Step 2:** Lấy ra last\_accessed\_lesson\_id. Nếu null (nghĩa là chưa học bài nào), ném lỗi hoặc trả về ID bài đầu tiên.
    
*   **Step 3:** Dùng last\_accessed\_lesson\_id đó query vào bảng LessonProgress để lấy ra watch\_position\_seconds.
    
*   **Step 4:** Trả data về cho Frontend.
    

**Đặc tả API:**

**MethodEndpointResponse (Thành công)**GET/api/learning/courses/{courseId}/resume{ "lessonId": "uuid-...", "resumeAtSeconds": 120 }