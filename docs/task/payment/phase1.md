🚦 Vòng đời Dòng tiền (The Cash Flow)
-------------------------------------

Trước khi chia Phase, em cần hình dung luồng đi của một đơn hàng trong kiến trúc Microservices:

Student chọn khóa học $\\rightarrow$ Áp mã giảm giá $\\rightarrow$ Tạo Order (Pending) $\\rightarrow$ Gọi Cổng thanh toán (VNPay/Momo/Stripe) $\\rightarrow$ Webhook trả kết quả $\\rightarrow$ Cập nhật Order (Success) $\\rightarrow$ **Bắn Event RabbitMQ để Core Service ghi danh (Enroll) học viên.**

### 🎯 Phase 1: Nền tảng Giá & Khuyến mãi (Coupon Engine)

_Phải tính được đúng số tiền cần thu trước khi đưa khách đi thanh toán._

1.  **Admin - Create Coupon:** Admin tạo mã giảm giá (Theo %, theo số tiền cố định, giới hạn số lần dùng, ngày hết hạn).
    
2.  **Student - Apply Coupon:** Học viên nhập mã. Hệ thống tính toán lại tổng tiền (Grand Total). Cần lock (khóa) coupon tạm thời để tránh 1 mã giới hạn bị nhiều người xài cùng lúc.
    
3.  **Student - Remove Coupon:** Gỡ mã giảm giá, khôi phục lại giá gốc.
    

### 🎯 Phase 2: Thanh toán & Xử lý Giao dịch (Core Payment - Quan trọng nhất)

_Đây là chốt chặn quyết định tiền có vào tài khoản hay không._

1.  **Student - Checkout:** Chốt đơn hàng. Sinh ra một mã order\_id lưu DB với trạng thái PENDING. Tạo URL thanh toán từ bên thứ 3 (VNPay, Momo) và trả về cho Frontend redirect user đi.
    
2.  **Payment Gateway - Process Payment Callback (Webhook):** Cổng thanh toán gọi ngược lại server của em báo kết quả (Thành công / Thất bại).
    
    *   _Đòi hỏi kỹ thuật:_ Bắt buộc phải có cơ chế **Idempotency** (chống gọi trùng) và xác thực chữ ký số (Signature/Checksum) để chống hacker giả mạo webhook.
        
3.  **Student - Retry Payment:** Nếu thanh toán thất bại hoặc user tắt ngang trình duyệt, họ có thể vào lại đơn hàng cũ (đang PENDING) và bấm thanh toán lại.
    

### 🎯 Phase 3: Quản lý Đơn hàng & Hóa đơn (Order Management)

_Lưu vết lịch sử minh bạch cho cả Học viên và Admin._

1.  **Student - View Order History:** Danh sách các đơn hàng đã mua.
    
2.  **Student - View Order Details:** Xem chi tiết đơn hàng (Mua khóa nào, áp mã nào, thanh toán qua cổng nào).
    
3.  **Student - View Invoice:** Xuất hóa đơn điện tử (PDF) hoặc biên lai thanh toán.
    
4.  **Admin - View Transactions:** Bảng điều khiển cho kế toán/admin đối soát dòng tiền vào ra.
    

### 🎯 Phase 4: Xử lý Hoàn tiền (Refund Workflow)

_Luồng xử lý khi có khiếu nại hoặc học viên muốn trả lại khóa học._

1.  **Student - Request Refund:** Gửi yêu cầu hoàn tiền (kèm lý do). Trạng thái đơn hàng chuyển thành REFUND\_PENDING.
    
2.  **Admin - View Refund Requests:** Admin xem danh sách các đơn đang đòi hoàn tiền.
    
3.  **Admin - Approve Refund:** Chấp nhận hoàn tiền. Gọi API Refund của VNPay/Momo để trả tiền về thẻ cho khách. Đổi trạng thái thành REFUNDED và bắn Event RabbitMQ để **hủy ghi danh (Un-enroll)** khóa học.
    
4.  **Admin - Reject Refund:** Từ chối hoàn tiền, đơn hàng quay lại trạng thái COMPLETED.