2\. Student - Submit Exam (Nộp bài & Chấm tự động)
--------------------------------------------------

**Mô tả:** Học viên nộp toàn bộ đáp án. Backend sẽ hứng mảng đáp án, so sánh với DB để chấm điểm ngay lập tức cho các câu Trắc nghiệm (MCQ).

**Ý tưởng thiết kế (Cực kỳ quan trọng):**

Phải có cơ chế **Grace Period (Thời gian ân hạn)**. Nếu đề thi 60 phút, mạng lag học viên nộp vào phút 60 + 5 giây thì hệ thống vẫn nên châm chước nhận. Trễ quá 1 phút thì từ chối (báo Time Out).

**Logic nghiệp vụ (Business Logic):**

*   **Input:** attemptId, JSON chứa mảng các câu trả lời (questionId, selectedOptionIds, textAnswer).
    
*   **Step 1 - Security Time Check:** Kiểm tra NOW() có vượt quá end\_time + 1 phút ân hạn không. Nếu quá -> Quăng lỗi EXAM\_TIMEOUT.
    
*   **Step 2 - Auto-Grading (Chấm tự động):**
    
    *   Lặp qua danh sách câu trả lời.
        
    *   Nếu là loại SINGLE/MULTIPLE CHOICE: Lôi cấu trúc đáp án từ bảng Question ra so khớp. Đúng thì cộng điểm của câu đó vào biến totalAutoScore. Sai thì 0 điểm.
        
    *   Nếu là loại ESSAY (Tự luận): Bỏ qua chấm điểm, đánh cờ là câu này cần chấm tay.
        
*   **Step 3 - Cập nhật trạng thái:**
    
    *   Cập nhật mảng đáp án của học sinh (lưu dạng JSONB) vào ExamSubmission.
        
    *   Nếu đề thi **chỉ có trắc nghiệm** -> Chuyển status sang COMPLETED, lưu tổng điểm.
        
    *   Nếu đề thi **có tự luận** -> Chuyển status sang NEEDS\_GRADING (Chờ chấm điểm).
        

**Đặc tả API:**

**MethodEndpointPayload mẫuPOST**/api/learning/exams/attempts/{attemptId}/submit{ "answers": \[ { "questionId": "q1", "selectedIds": \["A"\] }, { "questionId": "q2", "textAnswer": "Bởi vì..." } \] }