package com.tommy.identity.application.service;

import com.tommy.identity.application.dto.request.LoginRequest;
import com.tommy.identity.application.dto.request.LogoutRequest;
import com.tommy.identity.application.dto.request.RefreshTokenRequest;
import com.tommy.identity.application.dto.request.RegisterRequest;
import com.tommy.identity.application.dto.response.AuthResponse;

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
