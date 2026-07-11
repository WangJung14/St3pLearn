3\. Teacher - Create / Update / Delete Exam (Lắp ráp Đề thi)
------------------------------------------------------------

**Mô tả:** Giáo viên bốc các câu hỏi từ một hoặc nhiều Ngân hàng để ráp thành một Đề thi hoàn chỉnh. Thiết lập các luật chơi như: Thời gian làm bài, Điểm qua môn, Số lần làm lại tối đa.

**Ý tưởng thiết kế:**

Đề thi (Exam) sẽ là một thực thể độc lập, sau đó có thể được đính kèm vào khóa học dưới dạng một bài học đặc biệt (ví dụ LessonType.EXAM). Cần một bảng phụ exam\_questions để mapping quan hệ N-N giữa Đề thi và Câu hỏi.

**Logic nghiệp vụ (Business Logic):**

*   **Create Exam:**
    
    *   Input: title, timeLimitMinutes, passingScore, maxAttempts, List questionIds.
        
    *   _Validate:_ Duyệt qua danh sách questionIds, đảm bảo tất cả các ID này đều có thật, chưa bị Soft Delete, và thuộc quyền sở hữu của Giáo viên đang request.
        
    *   Lưu thông tin chung vào bảng exams, và thực hiện **Batch Insert** danh sách câu hỏi vào bảng exam\_questions (có thêm cột order\_index để giữ thứ tự câu hỏi trong đề).
        
*   **Update Exam:**
    
    *   Cho phép Giáo viên kéo thả, đổi thứ tự câu hỏi, thêm câu mới hoặc bớt câu cũ.
        
    *   _Khóa an toàn (State Machine):_ Đề thi nên có trạng thái DRAFT và PUBLISHED. Nếu đang ở PUBLISHED (đã có học sinh thi), chặn đứng mọi hành vi Thêm/Sửa/Xóa câu hỏi để đảm bảo tính công bằng. Chỉ cho phép sửa tiêu đề hoặc mô tả.
        
*   **Delete Exam:**
    
    *   Không cho xóa nếu đã có học sinh làm bài (có dữ liệu trong bảng lịch sử thi). Chỉ được đổi trạng thái thành ARCHIVED (Lưu trữ - không cho thi nữa).
        

**Đặc tả API:**

**MethodEndpointPayload chínhPOST**/api/learning/exams{ "title": "Midterm", "timeLimit": 60, "questionIds": \["uuid1", "uuid2"\] }**PUT**/api/learning/exams/{examId}/questionsCập nhật lại danh sách và thứ tự câu hỏi**PUT**/api/learning/exams/{examId}/statusChuyển trạng thái từ DRAFT sang PUBLISHED