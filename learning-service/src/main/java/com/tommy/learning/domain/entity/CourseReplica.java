package com.tommy.learning.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "course_replicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseReplica {
    @Id
    private UUID id;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "total_lessons", nullable = false)
    @Builder.Default
    private Integer totalLessons = 0;

    @Column(name = "total_duration", nullable = false)
    @Builder.Default
    private Integer totalDuration = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lesson_ids", columnDefinition = "jsonb")
    private List<UUID> lessonIds;

}
