package com.tommy.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lesson_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonComment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "lesson_id", nullable = false)
    private UUID lessonId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_full_name", nullable = false)
    private String userFullName;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "parent_comment_id")
    private UUID parentCommentId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
