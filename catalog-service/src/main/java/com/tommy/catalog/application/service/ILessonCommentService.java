package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.CommentRequest;
import com.tommy.catalog.application.dto.response.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface ILessonCommentService {
    List<CommentResponse> getCommentsByLesson(UUID lessonId);
    CommentResponse createComment(UUID lessonId, UUID userId, String userFullName, String userRole, CommentRequest request);
    CommentResponse updateComment(UUID commentId, UUID userId, String userRole, CommentRequest request);
    void deleteComment(UUID commentId, UUID userId, String userRole);
}
