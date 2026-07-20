5\. Student - View Exam Result (Xem kết quả thi)
------------------------------------------------

**Mô tả:** Học viên xem lại thành quả của mình. Hiển thị % đúng sai, điểm số và đặc biệt là đọc lời phê của thầy cô.

**Logic nghiệp vụ (Business Logic):**

*   **Input:** attemptId, studentId (từ Token).
    
*   **Step 1:** Lấy ra ExamSubmission. Validate đảm bảo bài thi này đúng là của studentId đang request (tránh tình trạng học sinh A sửa URL xem trộm điểm học sinh B).
    
*   **Step 2:**
    
    *   Nếu status là NEEDS\_GRADING: Chỉ trả về thông báo "Bài thi đang chờ giáo viên chấm". KHÔNG trả điểm tổng.
        
    *   Nếu status là COMPLETED: Trả về toàn bộ JSONB đáp án (lúc này đã chứa teacherPoints, feedback và hiển thị lộ ra isCorrect để học sinh đối chiếu xem mình sai ở đâu).
        

**Đặc tả API:**

**MethodEndpointResponse quan trọngGET**/api/learning/exams/attempts/{attemptId}/resultCục JSON chi tiết điểm số, lời giải và lời phê.