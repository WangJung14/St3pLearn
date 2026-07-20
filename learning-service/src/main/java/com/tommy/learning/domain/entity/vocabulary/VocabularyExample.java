package com.tommy.learning.domain.entity.vocabulary;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "vocabulary_example")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VocabularyExample {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String sentence;
    
    @Column(columnDefinition = "TEXT")
    private String translation;
}
