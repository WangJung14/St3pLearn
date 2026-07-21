package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.dto.request.CommentRequest;
import com.tommy.catalog.application.dto.response.CommentResponse;
import com.tommy.catalog.application.service.ILessonCommentService;
import com.tommy.catalog.domain.entity.LessonComment;
import com.tommy.catalog.infrastructure.persistence.repository.LessonCommentRepository;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonCommentServiceImpl implements ILessonCommentService {

    private final LessonCommentRepository commentRepository;

    @Override
    public List<CommentResponse> getCommentsByLesson(UUID lessonId) {
        log.info("Fetching comments for lesson {}", lessonId);
        List<LessonComment> allComments = commentRepository.findByLessonIdOrderByCreatedAtAsc(lessonId);

        // Convert all to responses
        List<CommentResponse> responses = allComments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        // Group by parentCommentId
        Map<UUID, List<CommentResponse>> repliesMap = responses.stream()
                .filter(r -> r.getParentCommentId() != null)
                .collect(Collectors.groupingBy(CommentResponse::getParentCommentId));

        // Set replies for each response
        responses.forEach(r -> r.setReplies(repliesMap.getOrDefault(r.getId(), new ArrayList<>())));

        // Return only root comments
        return responses.stream()
                .filter(r -> r.getParentCommentId() == null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse createComment(UUID lessonId, UUID userId, String userFullName, String userRole, CommentRequest request) {
        log.info("User {} creating comment for lesson {}", userId, lessonId);

        // If parent comment is provided, check if it exists
        if (request.getParentCommentId() != null) {
            commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        }

        LessonComment comment = LessonComment.builder()
                .lessonId(lessonId)
                .userId(userId)
                .userFullName(userFullName)
                .userRole(userRole)
                .content(request.getContent())
                .parentCommentId(request.getParentCommentId())
                .build();

        LessonComment saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(UUID commentId, UUID userId, String userRole, CommentRequest request) {
        log.info("User {} updating comment {}", userId, commentId);
        LessonComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        // Check ownership (Admin can edit anything, otherwise must be the owner)
        if (!"ADMIN".equals(userRole) && !comment.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        comment.setContent(request.getContent());
        LessonComment saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId, UUID userId, String userRole) {
        log.info("User {} deleting comment {}", userId, commentId);
        LessonComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        // Check permission (Admin and Teacher can delete anything, otherwise must be the owner)
        if (!"ADMIN".equals(userRole) && !"TEACHER".equals(userRole) && !comment.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Delete all child replies recursively (simplistic implementation: delete direct replies first)
        List<LessonComment> directReplies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(commentId);
        commentRepository.deleteAll(directReplies);

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(LessonComment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .lessonId(comment.getLessonId())
                .userId(comment.getUserId())
                .userFullName(comment.getUserFullName())
                .userRole(comment.getUserRole())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(new ArrayList<>())
                .build();
    }
}
