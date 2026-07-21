package com.tommy.payment.infrastructure.client;

import com.tommy.common.response.ApiResponse;
import com.tommy.payment.infrastructure.client.dto.CatalogCourseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "catalog-service")
public interface CatalogClient {
    @GetMapping("/api/courses/{courseId}")
    ApiResponse<CatalogCourseResponse> getCourse(@PathVariable UUID courseId);
}
