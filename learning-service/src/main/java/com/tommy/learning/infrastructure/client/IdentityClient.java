package com.tommy.learning.infrastructure.client;

import com.tommy.common.response.ApiResponse;
import com.tommy.learning.infrastructure.client.dto.IdentityUserDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "identity-service", path = "/api/users")
public interface IdentityClient {

    @GetMapping("/{userId}")
    ResponseEntity<ApiResponse<IdentityUserDetailResponse>> getUserById(@PathVariable("userId") UUID userId);
}
