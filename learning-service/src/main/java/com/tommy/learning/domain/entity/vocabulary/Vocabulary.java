package com.tommy.learning.domain.entity.vocabulary;

import com.tommy.learning.domain.enums.CefrLevel;
import com.tommy.learning.domain.enums.SourceEnum;
import com.tommy.learning.domain.enums.VisibilityEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vocabulary", indexes = {
    @Index(name = "idx_vocab_lemma_lang", columnList = "lemma, language")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String lemma;

    @Column(nullable = false)
    private String language;

    @Column
    private String phonetic;

    @Column(name = "part_of_speech")
    private String partOfSpeech;

    @Enumerated(EnumType.STRING)
    @Column(name = "cefr_level")
    private CefrLevel cefrLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceEnum source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisibilityEnum visibility;

    @Builder.Default
    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VocabularyMeaning> meanings = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VocabularyExample> examples = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VocabularyAudio> audios = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VocabularyImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Version
    private Long version;
}
