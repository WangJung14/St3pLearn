package com.tommy.catalog.domain.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "lesson_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonContent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private CourseLesson lesson;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType; // VIDEO_CLOUDINARY, PDF_SUPABASE, YOUTUBE_EMBED...

    @Column(name = "storage_url", nullable = false, columnDefinition = "TEXT")
    private String storageUrl;

    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;

    @Column(name = "file_size")
    private Long fileSize; // Calculate by byte

    @Column(length = 255)
    private String checksum;

    // Map a PostgreSQL JSONB column to a Java Map.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
}