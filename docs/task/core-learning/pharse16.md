TÀI LIỆU ĐẶC TẢ KỸ THUẬT: VOCABULARY & FLASHCARD SYSTEM
=======================================================

**Mục tiêu:** Xây dựng tính năng học từ vựng theo mô hình lặp lại ngắt quãng (Spaced Repetition - SM-2), kiến trúc mở để dễ dàng scale sang các phương pháp học khác trong tương lai.

I. KIẾN TRÚC DỮ LIỆU TỔNG THỂ (DATA ARCHITECTURE)
-------------------------------------------------

Hệ thống được chia làm 3 Domain chính:

1.  **Vocabulary Domain:** Lõi dữ liệu từ vựng nguyên thủy (Không phụ thuộc phương pháp học).
    
2.  **Flashcard Domain:** Hình thức đóng gói dữ liệu để học viên tương tác.
    
3.  **Progress Domain:** Lưu trữ tiến độ và lịch sử học thuật toán SM-2.
    

Plaintext

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   📦 SYSTEM DATABASE   ┣ 📂 VOCABULARY CORE   ┃ ┣ 📜 vocabulary (Bảng gốc chứa từ vựng)   ┃ ┣ 📜 vocabulary_meaning (Nhiều nghĩa của 1 từ)   ┃ ┣ 📜 vocabulary_example (Ví dụ minh họa)   ┃ ┣ 📜 vocabulary_audio (File phát âm US, UK)   ┃ ┗ 📜 vocabulary_image (Hình ảnh minh họa)   ┃   ┣ 📂 FLASHCARD MODULE   ┃ ┣ 📜 flashcard (Định nghĩa mặt trước/mặt sau)   ┃ ┣ 📜 flashcard_set (Bộ thẻ)   ┃ ┗ 📜 flashcard_set_card (Mapping N-N giữa Thẻ và Bộ)   ┃   ┗ 📂 STUDENT PROGRESS     ┣ 📜 flashcard_progress (Tiến độ hiện tại của SM-2)     ┗ 📜 flashcard_review_history (Nhật ký lật thẻ - Dùng cho Analytics)   `

II. THIẾT KẾ DATABASE (SCHEMA DESIGN)
-------------------------------------

_Lưu ý: Tất cả các bảng đều có các trường Audit cơ bản: created\_at, updated\_at, deleted\_at (Soft Delete), version (Optimistic Locking)._

### 1\. Nhóm Bảng Vocabulary Core (Dữ liệu gốc)

**Bảng vocabulary**

**FieldTypeNote**idUUID (PK)lemmaStringTừ gốc (vd: run, apple)languageStringvd: EN, DE, VIphoneticStringPhiên âm IPApart\_of\_speechStringNoun, Verb, Adj...cefr\_levelStringA1, A2, B1, B2, C1, C2sourceEnumOXFORD, CAMBRIDGE, SYSTEM, USER\_IMPORTvisibilityEnumPUBLIC, PRIVATE, SYSTEM

**Bảng vocabulary\_meaning** (1 từ có nhiều nghĩa)

**FieldTypeNote**idUUID (PK)vocabulary\_idUUID (FK)Trỏ về bảng vocabularydefinitionTextĐịnh nghĩa chi tiếtnoteStringGhi chú thêm

_(Các bảng vocabulary\_example, vocabulary\_audio, vocabulary\_image thiết kế tương tự, liên kết vocabulary\_id N-1)._

### 2\. Nhóm Bảng Flashcard Module (Đóng gói nội dung học)

**Bảng flashcard** (Quyết định mặt trước/mặt sau hiển thị gì)

**FieldTypeNote**idUUID (PK)vocabulary\_idUUID (FK)Link tới từ vựng gốcfront\_typeEnumWORD, AUDIO, IMAGEback\_typeEnumMEANING, EXAMPLE, WORDcreated\_byUUIDGiáo viên tạo thẻ

**Bảng flashcard\_set** (Bộ thẻ)

**FieldTypeNote**idUUID (PK)titleStringTên bộ (vd: IELTS 500)course\_idUUID (FK)Có thể null nếu là bộ độc lậpinstructor\_idUUIDNgười sở hữu bộ thẻvisibilityEnumPRIVATE, PUBLIC, COURSE\_ONLY

**Bảng flashcard\_set\_card** (Quan hệ N-N)

**FieldTypeNote**set\_idUUID (FK)flashcard\_idUUID (FK)display\_orderIntegerThứ tự sắp xếp (nếu có)

### 3\. Nhóm Bảng Student Progress (Trái tim hệ thống)

**Bảng flashcard\_progress** (Tiến độ hiện tại - 1 dòng cho 1 User + 1 Card)

**FieldTypeNote**student\_idUUID (FK)flashcard\_idUUID (FK)repetitionIntegerSố lần lặp lạiinterval\_daysIntegerKhoảng cách ngày ôneasiness\_factorFloatHệ số dễ (Mặc định 2.5)next\_review\_dateTimestampHạn ôn tập tiếp theolearning\_stateEnumNEW, LEARNING, REVIEW, MASTERED

**Bảng flashcard\_review\_history** (Log Data cho Analytics)

**FieldTypeNote**student\_idUUIDflashcard\_idUUIDquality\_scoreIntegerĐiểm học viên tự vote (0-5)ef\_beforeFloatHệ số EF trước khi lậtef\_afterFloatHệ số EF sau khi tính toánreview\_timeTimestampThời điểm lật thẻ

III. THUẬT TOÁN SM-2 & LOGIC CHẤM ĐIỂM (CORE ENGINE)
----------------------------------------------------

Khi User nộp qualityScore ($q$) từ 0 đến 5, Backend thực hiện tính toán:

**1\. Cập nhật Repetition & Interval:**

*   Nếu $q < 3$ (Quên): repetition = 0, interval = 1
    
*   Nếu $q \\ge 3$ (Nhớ):
    
    *   Nếu repetition == 0 $\\rightarrow$ interval = 1
        
    *   Nếu repetition == 1 $\\rightarrow$ interval = 6
        
    *   Nếu repetition > 1 $\\rightarrow$ interval = interval \* EF (làm tròn)
        
    *   repetition++
        

**2\. Cập nhật Easiness Factor (EF):**

Áp dụng công thức:

$$EF\_{new} = EF\_{old} + (0.1 - (5 - q) \\times (0.08 + (5 - q) \\times 0.02))$$

_(Bắt buộc: Nếu $EF\_{new} < 1.3$, set cứng $EF = 1.3$)_

**3\. Cập nhật Next Review Date:**

next\_review\_date = today + interval

IV. API SPECIFICATION (CONTRACTS)
---------------------------------

### 1\. Teacher Module (Quản trị & Đóng gói)

**MethodEndpointNotePOST**/api/learning/vocabulary/importPipeline import CSV $\\rightarrow$ Parser $\\rightarrow$ Validator $\\rightarrow$ Normalizer $\\rightarrow$ DupCheck.**POST**/api/learning/flashcard-setsTạo bộ thẻ**POST**/api/learning/flashcard-sets/{id}/cloneNhân bản bộ thẻ**POST**/api/learning/flashcardsTạo thẻ mới (Link với Vocabulary)

### 2\. Student Module (Học tập & Tương tác)

**MethodEndpointNoteGET**/api/learning/flashcard-sets/{id}/due-cardsQuery lấy thẻ: next\_review\_date <= today HOẶC thẻ mới tinh.**POST**/api/learning/flashcards/{id}/reviewNộp { "qualityScore": 4 }. Chạy SM-2.**GET**/api/learning/dashboard/historyTrả về tổng hợp: Heatmap 30 ngày, Hardest Words, Review Count.

V. SYSTEM CONSTRAINTS & OPTIMIZATION (YÊU CẦU BẮT BUỘC)
-------------------------------------------------------

**1\. Tính toàn vẹn dữ liệu (Transaction):**

API /review bắt buộc bọc trong 1 Transaction duy nhất:

*   BEGIN $\\rightarrow$ Tính SM-2 $\\rightarrow$ Insert History $\\rightarrow$ Update Progress $\\rightarrow$ COMMIT. Bất kỳ step nào lỗi $\\rightarrow$ ROLLBACK.
    

**2\. Phân quyền (RBAC):**

*   Giáo viên không được phép Update/Delete các Vocabulary có source là SYSTEM, OXFORD, CAMBRIDGE.
    
*   Giáo viên chỉ được thao tác trên FlashcardSet do chính mình tạo ra.
    

**3\. Xóa mềm (Soft Delete):**

Áp dụng Soft Delete cho Vocabulary, Flashcard và FlashcardSet. Tuyệt đối không xóa cứng (Hard delete) để tránh làm sụp đổ bảng Progress và History của học sinh.

**4\. Đánh chỉ mục Database (Indexes) tối ưu truy vấn:**

*   vocabulary: Index trên (lemma, language)
    
*   flashcard\_progress: Index ghép (student\_id, next\_review\_date)
    
*   flashcard\_review\_history: Index ghép (student\_id, review\_time)
    
*   flashcard\_set\_card: Index ghép (set\_id, flashcard\_id)