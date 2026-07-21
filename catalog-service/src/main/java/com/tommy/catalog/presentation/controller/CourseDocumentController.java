package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.DocumentCallbackRequest;
import com.tommy.catalog.application.dto.request.ReviewDocumentRequest;
import com.tommy.catalog.application.dto.response.ApiResponse;
import com.tommy.catalog.domain.entity.CourseDocument;
import com.tommy.catalog.domain.enums.DocumentStatus;
import com.tommy.catalog.infrastructure.persistence.repository.CourseDocumentRepository;
import com.tommy.common.security.RequireRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseDocumentController {

    private final CourseDocumentRepository documentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String UPLOAD_DIR = "uploads/documents/";

    // 1. Giáo viên Upload File (.docx, .pdf, .txt) trực tiếp lên server local
    @PostMapping("/{courseId}/documents/upload")
    @RequireRole({"TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<CourseDocument>> uploadDocument(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-Id") UUID instructorId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title) {

        log.info("Instructor {} uploading document for course {}", instructorId, courseId);

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
                    : "docx";

            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), filePath);

            CourseDocument doc = CourseDocument.builder()
                    .courseId(courseId)
                    .title((title != null && !title.trim().isEmpty()) ? title : originalFilename)
                    .fileUrl(filePath.toAbsolutePath().toString())
                    .fileType(extension)
                    .status(DocumentStatus.PENDING_APPROVAL)
                    .uploadedBy(instructorId)
                    .build();

            CourseDocument saved = documentRepository.save(doc);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(201, "Tài liệu đã được tải lên và đang chờ Admin duyệt", saved));

        } catch (IOException e) {
            log.error("Lỗi khi lưu file local", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Không thể lưu tệp trên server local: " + e.getMessage()));
        }
    }

    // 2. Giáo viên nhập Text trực tiếp cho AI
    @PostMapping("/{courseId}/documents/text")
    @RequireRole({"TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<CourseDocument>> createTextDocument(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-Id") UUID instructorId,
            @RequestBody Map<String, String> body) {

        String title = body.getOrDefault("title", "Tài liệu kiến thức thô");
        String textContent = body.getOrDefault("textContent", "");

        CourseDocument doc = CourseDocument.builder()
                .courseId(courseId)
                .title(title)
                .fileType("txt")
                .textContent(textContent)
                .status(DocumentStatus.PENDING_APPROVAL)
                .uploadedBy(instructorId)
                .build();

        CourseDocument saved = documentRepository.save(doc);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Nội dung văn bản đã được ghi nhận và đang chờ Admin duyệt", saved));
    }

    // 3. Admin lấy danh sách tài liệu đang chờ duyệt
    @GetMapping("/documents/pending")
    @RequireRole({"ADMIN"})
    public ResponseEntity<ApiResponse<List<CourseDocument>>> getPendingDocuments() {
        List<CourseDocument> pendingDocs = documentRepository.findByStatus(DocumentStatus.PENDING_APPROVAL);
        return ResponseEntity.ok(ApiResponse.success(200, "Danh sách tài liệu chờ duyệt", pendingDocs));
    }

    // 4. Lấy danh sách tài liệu thuộc khóa học
    @GetMapping("/{courseId}/documents")
    public ResponseEntity<ApiResponse<List<CourseDocument>>> getCourseDocuments(@PathVariable UUID courseId) {
        List<CourseDocument> docs = documentRepository.findByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success(200, "Danh sách tài liệu của khóa học", docs));
    }

    // 5. Admin Phê duyệt hoặc Từ chối tài liệu
    @PutMapping("/documents/{documentId}/review")
    @RequireRole({"ADMIN"})
    public ResponseEntity<ApiResponse<CourseDocument>> reviewDocument(
            @PathVariable UUID documentId,
            @RequestBody ReviewDocumentRequest request) {

        CourseDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu ID: " + documentId));

        if (request.isApproved()) {
            doc.setStatus(DocumentStatus.PROCESSING);
            documentRepository.save(doc);

            // Gọi bất đồng bộ sang FastAPI ai-service
            CompletableFuture.runAsync(() -> {
                try {
                    String aiServiceUrl = "http://127.0.0.1:7777/api/ai/documents/ingest";
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("document_id", doc.getId().toString());
                    payload.put("course_id", doc.getCourseId().toString());
                    payload.put("file_url", doc.getFileUrl());
                    payload.put("file_type", doc.getFileType());
                    payload.put("text_content", doc.getTextContent());
                    payload.put("callback_url", "http://localhost:8082/api/courses/documents/" + doc.getId() + "/status-callback");

                    log.info("Sending async ingestion request to FastAPI for doc {}", doc.getId());
                    restTemplate.postForEntity(aiServiceUrl, payload, String.class);
                } catch (org.springframework.web.client.HttpStatusCodeException e) {
                    log.error("Lỗi HTTP {} từ FastAPI: {}", e.getStatusCode(), e.getResponseBodyAsString());
                    doc.setStatus(DocumentStatus.FAILED);
                    doc.setRejectReason("Lỗi FastAPI 500: " + e.getResponseBodyAsString());
                    documentRepository.save(doc);
                } catch (Exception e) {
                    log.error("Lỗi khi gửi yêu cầu sang FastAPI ai-service", e);
                    doc.setStatus(DocumentStatus.FAILED);
                    doc.setRejectReason(e.getMessage());
                    documentRepository.save(doc);
                }
            });

            return ResponseEntity.ok(ApiResponse.success(200, "Đã phê duyệt tài liệu. AI đang tiến hành nạp tri thức ngầm.", doc));
        } else {
            doc.setStatus(DocumentStatus.REJECTED);
            doc.setRejectReason(request.getRejectReason());
            CourseDocument saved = documentRepository.save(doc);
            return ResponseEntity.ok(ApiResponse.success(200, "Đã từ chối tài liệu", saved));
        }
    }

    // 6. Callback Endpoint cho FastAPI cập nhật kết quả
    @PutMapping("/documents/{documentId}/status-callback")
    public ResponseEntity<ApiResponse<Void>> updateStatusCallback(
            @PathVariable UUID documentId,
            @RequestBody DocumentCallbackRequest request) {

        log.info("Received callback from FastAPI for document {}: status={}", documentId, request.getStatus());
        CourseDocument doc = documentRepository.findById(documentId).orElse(null);
        if (doc != null) {
            if ("INGESTED".equalsIgnoreCase(request.getStatus())) {
                doc.setStatus(DocumentStatus.INGESTED);
                doc.setChunkCount(request.getChunkCount());
            } else {
                doc.setStatus(DocumentStatus.FAILED);
                doc.setRejectReason(request.getError());
            }
            documentRepository.save(doc);
        }
        return ResponseEntity.ok(ApiResponse.success(200, "Callback updated", null));
    }
}
