package com.tommy.learning.domain.entity.flashcard;

import com.tommy.learning.domain.enums.LearningState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "flashcard_progress", indexes = {
    @Index(name = "idx_fp_student_next_review", columnList = "student_id, next_review_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @Column(nullable = false)
    @Builder.Default
    private Integer repetition = 0;

    @Column(name = "interval_days", nullable = false)
    @Builder.Default
    private Integer intervalDays = 0;

    @Column(name = "easiness_factor", nullable = false)
    @Builder.Default
    private Float easinessFactor = 2.5f;

    @Column(name = "next_review_date", nullable = false)
    @Builder.Default
    private LocalDateTime nextReviewDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "learning_state", nullable = false)
    @Builder.Default
    private LearningState learningState = LearningState.NEW;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
}
