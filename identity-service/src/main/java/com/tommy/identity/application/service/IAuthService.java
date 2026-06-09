package com.tommy.identity.application.service;

import com.tommy.identity.application.dto.*;

public interface IAuthService {

    /**
     * Xử lý nghiệp vụ đăng ký tài khoản mới cho hệ thống
     * @param request Dữ liệu đầu vào từ người dùng
     * @return Thông báo trạng thái đăng ký
     */
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
    /**
    * Reissue access token base on refresh token
    * */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     *Logout Account
     * */
    void logout(LogoutRequest request);
}
