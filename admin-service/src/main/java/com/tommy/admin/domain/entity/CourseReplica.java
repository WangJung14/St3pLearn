package com.tommy.admin.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    
    private String title;
    private UUID teacherId;
    private BigDecimal price;
    private String status;
    
    private LocalDateTime createdAt;
    
    // Aggregated metrics
    @Builder.Default
    private Long enrollmentCount = 0L;
    
    @Builder.Default
    private Long completionCount = 0L;
    
    @Builder.Default
    private Double averageRating = 0.0;
    
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;
}
