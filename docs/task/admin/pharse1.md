### 🎯 Phase 1: Nền tảng Báo cáo & Phân tích (Analytics & Reports)

_Đây là thứ mà các sếp (Stakeholders) và chủ hệ thống muốn nhìn thấy nhất mỗi ngày. Nó tổng hợp dữ liệu từ Learning Service, Payment Service và User Service._

1.  **Admin - View Dashboard:** Hiển thị các chỉ số tổng quan (High-level metrics) như: Tổng doanh thu tháng, số học viên mới hôm nay, khóa học bán chạy nhất, số lượng bài report đang chờ xử lý.
    
2.  **Admin - Generate Revenue Report:** Phân tích doanh thu sâu hơn (theo ngày/tháng/năm, theo khóa học, theo giáo viên).
    
3.  **Admin - Generate User Report:** Thống kê tăng trưởng người dùng, tỷ lệ active, tỷ lệ hoàn thành khóa học trung bình.
    
4.  **Admin - Generate Course Report:** Thống kê hiệu suất khóa học (Rating trung bình, tỷ lệ bỏ cuộc, số lượng hoàn thành).
    
5.  **Admin - Export Reports:** Xuất các báo cáo trên ra file CSV hoặc PDF để kế toán hoặc ban giám đốc họp.
    

### 🎯 Phase 2: Hệ thống Kiểm duyệt & Xử lý vi phạm (Content Moderation)

_Khi hệ thống lớn lên, sẽ có người dùng spam, bình luận bậy bạ hoặc khóa học vi phạm bản quyền. Đây là lúc đội ngũ Moderator (Điều phối viên) vào việc._

1.  **Moderator - Review Reported Content:** Lướt xem danh sách các nội dung bị người dùng report (ví dụ: Bình luận xấu, Flashcard chứa từ lóng...).
    
2.  **Moderator - Open Moderation Case:** Khởi tạo một "Vụ án" (Case) để điều tra chi tiết một vi phạm.
    
3.  **Moderator - Dismiss Moderation Case:** Đóng case nếu phát hiện đó là report sai (False alarm).
    
4.  **Moderator / Admin - Resolve Moderation Case:** Đưa ra phán quyết cuối cùng (Ví dụ: Xóa bình luận, cảnh cáo học sinh, khóa tài khoản giáo viên). Admin có quyền cao hơn Mod trong việc ra quyết định.
    
5.  **Admin - View Moderation Cases:** Xem lại toàn bộ lịch sử xử lý của các Moderator để đánh giá hiệu quả làm việc của họ.
    

### 🎯 Phase 3: Quản trị Hệ thống & Lưu vết (System Admin & Audit)

_Nhóm tính năng bảo vệ hệ thống khỏi những thay đổi không kiểm soát (cực kỳ quan trọng để truy vết "Ai đã làm gì?")._

1.  **Admin - Manage System Configurations:** Chỉnh sửa các cấu hình động của toàn hệ thống mà không cần deploy lại code. (Ví dụ: Đổi % hoa hồng chia cho giáo viên, cập nhật banner trang chủ, bật/tắt cổng thanh toán Momo).
    
2.  **Admin - View Audit Logs:** Xem nhật ký hoạt động hệ thống. (Ví dụ: "Admin A đã sửa % hoa hồng từ 30% xuống 20% vào lúc 14:00", hoặc "Moderator B đã khóa tài khoản của học sinh C").