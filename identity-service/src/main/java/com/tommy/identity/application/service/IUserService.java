package com.tommy.identity.application.service;

import com.tommy.identity.application.dto.PublicProfileResponse;
import com.tommy.identity.application.dto.UserProfileResponse;

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

}
