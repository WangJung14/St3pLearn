package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.TagRequest;

import com.tommy.catalog.application.service.ITagService;
import com.tommy.catalog.domain.entity.Tag;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final ITagService tagService;

    @GetMapping // PUBLIC ACCESS
    public ResponseEntity<ApiResponse<List<Tag>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(tagService.getAllTags()));
    }

    @PostMapping
    @RequireRole("ADMIN")
    public ResponseEntity<ApiResponse<Tag>> create(@Valid @RequestBody TagRequest request) {
        Tag created = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<ApiResponse<Tag>> update(@PathVariable UUID id, @Valid @RequestBody TagRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tagService.updateTag(id, request)));
    }

    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok(ApiResponse.success(200, "Delete tag successful", null));
    }
}