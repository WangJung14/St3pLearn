package com.tommy.identity.application.service;

import com.tommy.identity.application.dto.request.AssignRoleRequest;

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
}
