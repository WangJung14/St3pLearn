package com.tommy.catalog.infrastructure.client;

import com.tommy.catalog.application.dto.response.ApiResponse;
import com.tommy.catalog.infrastructure.client.dto.IdentityUserDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "identity-service", url = "${application.config.identity-url:http://localhost:8080}")
public interface IdentityClient {

    @GetMapping("/api/admin/users/{userId}")
    ApiResponse<IdentityUserDetailResponse> getUserDetail(
            @PathVariable("userId") UUID userId,
            @RequestHeader("Authorization") String token);
}
