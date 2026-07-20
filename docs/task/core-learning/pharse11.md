4\. Teacher - Grade Submission & Text Feedback (Chấm tay & Phê bình)
--------------------------------------------------------------------

**Mô tả:** Giáo viên chấm điểm cho các câu tự luận và gõ lời phê cho học viên.

**Logic nghiệp vụ (Business Logic):**

*   **Input:** attemptId, mảng kết quả chấm (questionId, pointsAwarded, feedbackText).
    
*   **Step 1:** Tìm ExamSubmission (bắt buộc phải đang ở status NEEDS\_GRADING hoặc sửa điểm khi đã COMPLETED).
    
*   **Step 2:** Duyệt qua mảng kết quả giáo viên gửi lên. Cập nhật lại cái cục JSONB lưu đáp án ban nãy (bổ sung thêm key teacherPoints và feedback vào từng node câu hỏi).
    
*   **Step 3 - Finalize:** Tính lại tổng điểm cuối cùng = totalAutoScore + tổng điểm giáo viên chấm. Đổi status = COMPLETED.
    

**Đặc tả API:**

**MethodEndpointPayload mẫuPUT**/api/learning/exams/submissions/{attemptId}/grade{ "grades": \[ { "questionId": "q2", "pointsAwarded": 8.5, "feedbackText": "Ý tưởng tốt nhưng lập luận chưa chặt chẽ." } \] }