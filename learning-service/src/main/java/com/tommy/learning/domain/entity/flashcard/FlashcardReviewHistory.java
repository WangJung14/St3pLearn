package com.tommy.learning.domain.entity.flashcard;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "flashcard_review_history", indexes = {
    @Index(name = "idx_frh_student_time", columnList = "student_id, review_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardReviewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @Column(name = "quality_score", nullable = false)
    private Integer qualityScore;

    @Column(name = "ef_before", nullable = false)
    private Float efBefore;

    @Column(name = "ef_after", nullable = false)
    private Float efAfter;

    @Column(name = "review_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime reviewTime;
}
