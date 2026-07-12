package com.tommy.learning.infrastructure.client;

import com.tommy.common.response.ApiResponse;
import com.tommy.learning.infrastructure.client.dto.CatalogCourseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "catalog-service", path = "/api/catalog/courses")
public interface CatalogClient {

    @GetMapping("/{courseId}")
    ResponseEntity<ApiResponse<CatalogCourseResponse>> getCourse(@PathVariable("courseId") UUID courseId);
}
