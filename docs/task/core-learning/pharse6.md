2\. Teacher - Manage Questions (Quản lý chi tiết Câu hỏi)
---------------------------------------------------------

**Mô tả:** Thêm, sửa, xóa các câu hỏi bên trong một Ngân hàng. Hỗ trợ nhiều định dạng: Trắc nghiệm 1 đáp án (Single Choice), Trắc nghiệm nhiều đáp án (Multiple Choice), Tự luận (Essay),……

**Ý tưởng thiết kế (Tối ưu DB):**Vì mỗi loại câu hỏi có cấu trúc đáp án khác nhau, nếu em tạo từng cột cho đáp án A, B, C, D thì DB sẽ rất cứng nhắc. **Tuyệt chiêu ở đây là dùng kiểu dữ liệu JSONB của PostgreSQL** để lưu mảng các lựa chọn (Options) và giải thích (Explanation).

**Logic nghiệp vụ (Business Logic):**

*   **Entity Core:** Cần các trường bank\_id, type (Enum: SINGLE, MULTIPLE, ESSAY), content (nội dung câu hỏi), metadata (JSONB chứa các đáp án), points (điểm mặc định).
    
*   **Create Question:**
    
    *   Kiểm tra bankId có tồn tại và thuộc quyền sở hữu của Giáo viên không.
        
    *   Nếu là loại MCQ (Trắc nghiệm), phải validate chuỗi JSONB gửi lên có ít nhất 1 đáp án được đánh dấu là isCorrect = true.
        
*   **Update Question:**
    
    *   _Ràng buộc cốt lõi:_ Nếu câu hỏi này **chưa** được dùng trong đề thi nào -> Cho sửa thoải mái.
        
    *   Nếu câu hỏi **đã** nằm trong một đề thi (Exam) đang public -> Chỉ cho phép sửa lỗi chính tả, KHÔNG được phép đổi đáp án đúng (vì sẽ làm sai lệch điểm của học sinh đã thi trước đó). Nếu muốn đổi cấu trúc, phải tạo câu hỏi mới.
        
*   **Delete Question:**
    
    *   Tương tự Question Bank, bắt buộc dùng **Soft Delete**.
        

**Đặc tả JSONB metadata mẫu:**

JSON

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   {    "options": [      { "id": "A", "text": "Spring Boot", "isCorrect": true },      { "id": "B", "text": "Django", "isCorrect": false }    ],    "explanation": "Spring Boot là framework của Java."  }   `