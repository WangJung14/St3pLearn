package com.tommy.learning.domain.entity.vocabulary;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "vocabulary_audio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "vocabulary"}, ignoreUnknown = true)
public class VocabularyAudio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(name = "audio_url", nullable = false)
    private String audioUrl;

    @Column(name = "region_accent")
    private String regionAccent; // UK, US
}
