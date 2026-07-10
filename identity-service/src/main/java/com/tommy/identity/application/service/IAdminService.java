package com.tommy.identity.application.service;

import com.tommy.identity.application.dto.request.AssignRoleRequest;
import com.tommy.identity.application.dto.response.UserListAdminResponse;
import com.tommy.identity.domain.enums.AccountStatus;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IAdminService {
    /**
     * Gán Role cho User
     */
    void assignRole(UUID targetUserId, AssignRoleRequest request);

    /**
     * Xóa Role khỏi User
     */
    void removeRole(UUID targetUserId, String roleName);

    /**
     * Thay đổi trạng thái tài khoản (Suspend, Lock, Activate)
     */
    void changeAccountStatus(UUID targetUserId, AccountStatus status);

    /**
     * Tìm kiếm người dùng
     */
    Page<UserListAdminResponse> searchUsers(String keyword, int page, int size);
}
