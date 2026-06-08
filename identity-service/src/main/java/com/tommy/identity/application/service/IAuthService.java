package com.tommy.identity.application.service;

import com.tommy.identity.application.dto.AuthResponse;
import com.tommy.identity.application.dto.RegisterRequest;

public interface IAuthService {

    /**
     * Xử lý nghiệp vụ đăng ký tài khoản mới cho hệ thống
     * @param request Dữ liệu đầu vào từ người dùng
     * @return Thông báo trạng thái đăng ký
     */
    AuthResponse register(RegisterRequest request);

}
