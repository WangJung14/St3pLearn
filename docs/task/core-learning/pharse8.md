1\. Student - Take Exam (Bắt đầu làm bài thi)
---------------------------------------------

**Mô tả:** Khi học viên nhấn "Bắt đầu", hệ thống sẽ sinh ra một phiên làm bài (Attempt/Submission), bắt đầu đếm ngược thời gian và khóa các điều kiện nộp bài.

**Ý tưởng thiết kế:**

Tuyệt đối **KHÔNG** tin tưởng thời gian gửi từ Frontend. Thời gian bắt đầu và kết thúc phải do Backend (Server) quyết định và lưu trữ. Ta cần một Entity mới là ExamSubmission (hoặc ExamAttempt).

**Logic nghiệp vụ (Business Logic):**

*   **Input:** studentId, examId.
    
*   **Step 1 - Validate:** Check xem học viên đã ghi danh chưa, và số lần thi (attempts) đã vượt quá maxAttempts cấu hình trong bảng Exam chưa.
    
*   **Step 2 - Tạo Phiên thi:** Khởi tạo ExamSubmission với status = IN\_PROGRESS.
    
    *   Ghi nhận start\_time = NOW().
        
    *   Tính toán end\_time = NOW() + exam.timeLimitMinutes.
        
*   **Step 3 - Trả về dữ liệu thi:** Lấy danh sách câu hỏi (nhưng **PHẢI CHE ĐI** phần đáp án đúng isCorrect trong cột JSONB, nếu không Frontend soi Network tab là biết hết đáp án). Trả về kèm endTime để Frontend tự làm đồng hồ đếm ngược.
    

**Đặc tả API:**

**MethodEndpointResponse quan trọngPOST**/api/learning/exams/{examId}/attempts{ "attemptId": "uuid", "endTime": "2026-07-20T18:00:00", "questions": \[...\] }