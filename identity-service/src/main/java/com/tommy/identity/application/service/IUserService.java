package com.tommy.identity.application.service;

import com.tommy.identity.application.dto.request.UpdateProfileRequest;
import com.tommy.identity.application.dto.response.LoginHistoryResponse;
import com.tommy.identity.application.dto.response.PublicProfileResponse;
import com.tommy.identity.application.dto.response.UserProfileResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IUserService {
    /**
     * Lấy thông tin hồ sơ chi tiết và riêng tư của người dùng
     * @param userId ID trích xuất an toàn từ chuỗi JWT Token
     * @return Đối tượng chứa đầy đủ thông tin tài khoản và hồ sơ
     */
    UserProfileResponse getMyProfile(UUID userId);

    /**
     * Tìm user profile thông qua PublicId
     */
    PublicProfileResponse getPublicProfile(String publicId);

    /**
     * Cập nhật thông tin hồ sơ cá nhân user đang login
     * */
    UserProfileResponse updateMyProfile(UUID userId, UpdateProfileRequest request);

    /**
     * Vô hiệu hóa tài khoản và thu hồi phiên đăng nhập
     * */
    void deactivateMyAccount(UUID userId);

    /**
     * Xem lịch sử đăng nhập
     * */
    Page<LoginHistoryResponse> getMyLoginHistory(UUID userId, int page, int size);

}
