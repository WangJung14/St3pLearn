### **1\. Student - Track Learning Progress (Lưu vết tiến độ video)**

**Mô tả:** Khi Học viên đang xem video, Frontend sẽ âm thầm gọi API này định kỳ (ví dụ: cứ 10 giây gọi 1 lần) để báo cáo: "Anh ơi, học viên đang xem bài này đến giây thứ X rồi".

**Ý tưởng thiết kế:**

Phải cực kỳ cẩn thận với API này vì nó sẽ bị "spam" liên tục bởi hàng ngàn user cùng lúc. Cần tối ưu query DB, tránh update lắt nhắt quá nhiều.

**Logic nghiệp vụ (Business Logic):**

*   **Input:** studentId, courseId, lessonId, currentSeconds (số giây đang xem).
    
*   **Step 1:** Kiểm tra Enrollment xem học viên có quyền học không.
    
*   **Step 2:** Tìm LessonProgress dựa trên enrollmentId và lessonId.
    
*   **Step 3 (Cực kỳ quan trọng):** Kiểm tra currentSeconds gửi lên có lớn hơn watch\_position\_seconds hiện tại trong DB không.
    
    *   _Mẹo:_ Chỉ cập nhật tiến độ tiến tới, không cập nhật lùi (trường hợp học viên tua lại video để xem, ta vẫn giữ mốc cao nhất họ từng đạt được).
        
*   **Step 4:** Nếu trạng thái bài học đang là NOT\_STARTED, tự động chuyển thành IN\_PROGRESS.
    
*   **Step 5:** Cập nhật last\_accessed\_lesson\_id và last\_accessed\_at bên bảng Enrollment để đánh dấu bài học gần nhất user tương tác.
    

**Đặc tả API:**

**Method**

**Endpoint**

**Body/Payload**

POST

/api/learning/courses/{courseId}/lessons/{lessonId}/progress

{ "currentSeconds": 120 }