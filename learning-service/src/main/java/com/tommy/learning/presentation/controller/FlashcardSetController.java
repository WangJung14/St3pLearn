package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.learning.domain.entity.flashcard.Flashcard;
import com.tommy.learning.domain.entity.flashcard.FlashcardProgress;
import com.tommy.learning.domain.entity.flashcard.FlashcardSet;
import com.tommy.learning.domain.entity.flashcard.FlashcardSetCard;
import com.tommy.learning.domain.entity.vocabulary.Vocabulary;
import com.tommy.learning.domain.enums.BackType;
import com.tommy.learning.domain.enums.CefrLevel;
import com.tommy.learning.domain.enums.FrontType;
import com.tommy.learning.domain.enums.SourceEnum;
import com.tommy.learning.domain.enums.VisibilityEnum;
import com.tommy.learning.domain.enums.LearningState;
import com.tommy.learning.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/learning/flashcard-sets")
@RequiredArgsConstructor
@Slf4j
public class FlashcardSetController {

    private final FlashcardSetRepository flashcardSetRepository;
    private final FlashcardRepository flashcardRepository;
    private final VocabularyRepository vocabularyRepository;
    private final FlashcardSetCardRepository flashcardSetCardRepository;
    private final FlashcardProgressRepository progressRepository;

    public static class FlashcardSetCreateDto {
        public String title;
        public String visibility;
    }

    public static class WordAddDto {
        public String lemma;
        public String partOfSpeech;
        public String phonetic;
        public String definition;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FlashcardSet>>> getFlashcardSets(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(value = "publicOnly", required = false, defaultValue = "false") boolean publicOnly) {
        log.info("User {} getting flashcard sets, publicOnly={}", userId, publicOnly);
        List<FlashcardSet> sets;
        if (publicOnly) {
            sets = flashcardSetRepository.findByVisibilityAndDeletedAtIsNull(VisibilityEnum.PUBLIC);
        } else {
            sets = flashcardSetRepository.findByInstructorIdAndDeletedAtIsNull(userId);
        }
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched successfully", sets));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FlashcardSet>> createFlashcardSet(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody FlashcardSetCreateDto dto) {
        log.info("User {} creating flashcard set with title {}", userId, dto.title);
        
        VisibilityEnum visibility = VisibilityEnum.PRIVATE;
        try {
            visibility = VisibilityEnum.valueOf(dto.visibility.toUpperCase());
        } catch (Exception ignored) {}

        FlashcardSet newSet = FlashcardSet.builder()
                .title(dto.title)
                .instructorId(userId)
                .visibility(visibility)
                .setCards(new ArrayList<>())
                .build();
        
        newSet = flashcardSetRepository.save(newSet);
        return ResponseEntity.ok(ApiResponse.success(200, "Created successfully", newSet));
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<ApiResponse<FlashcardSet>> cloneFlashcardSet(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        log.info("User {} cloning flashcard set {}", userId, id);
        
        FlashcardSet sourceSet = flashcardSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flashcard set not found"));
        
        FlashcardSet clonedSet = FlashcardSet.builder()
                .title(sourceSet.getTitle() + " (Copy)")
                .instructorId(userId)
                .visibility(VisibilityEnum.PRIVATE)
                .setCards(new ArrayList<>())
                .build();
        
        clonedSet = flashcardSetRepository.save(clonedSet);
        
        for (FlashcardSetCard sourceCard : sourceSet.getSetCards()) {
            FlashcardSetCard clonedCard = FlashcardSetCard.builder()
                    .flashcardSet(clonedSet)
                    .flashcard(sourceCard.getFlashcard())
                    .build();
            clonedSet.getSetCards().add(clonedCard);
            
            // Initialize progress for student if it doesn't exist
            if (progressRepository.findByStudentIdAndFlashcardId(userId, sourceCard.getFlashcard().getId()).isEmpty()) {
                FlashcardProgress progress = FlashcardProgress.builder()
                        .studentId(userId)
                        .flashcard(sourceCard.getFlashcard())
                        .easinessFactor(2.5f)
                        .repetition(0)
                        .intervalDays(0)
                        .nextReviewDate(LocalDateTime.now())
                        .learningState(LearningState.NEW)
                        .build();
                progressRepository.save(progress);
            }
        }
        
        clonedSet = flashcardSetRepository.save(clonedSet);
        return ResponseEntity.ok(ApiResponse.success(200, "Cloned successfully", clonedSet));
    }

    @PostMapping("/{id}/words")
    public ResponseEntity<ApiResponse<Void>> addWordToSet(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id,
            @RequestBody WordAddDto dto) {
        log.info("User {} adding word {} to set {}", userId, dto.lemma, id);
        
        FlashcardSet set = flashcardSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flashcard set not found"));
        
        // Find or create vocabulary
        Vocabulary vocab = Vocabulary.builder()
                .lemma(dto.lemma)
                .language("en")
                .phonetic(dto.phonetic)
                .partOfSpeech(dto.partOfSpeech)
                .cefrLevel(CefrLevel.B1)
                .source(SourceEnum.SYSTEM)
                .visibility(VisibilityEnum.PUBLIC)
                .meanings(new ArrayList<>())
                .examples(new ArrayList<>())
                .images(new ArrayList<>())
                .audios(new ArrayList<>())
                .build();
        
        vocab = vocabularyRepository.save(vocab);
        
        // Create flashcard
        Flashcard card = Flashcard.builder()
                .vocabulary(vocab)
                .frontType(FrontType.WORD)
                .backType(BackType.MEANING)
                .createdBy(userId)
                .build();
        
        card = flashcardRepository.save(card);
        
        // Add to set
        FlashcardSetCard setCard = FlashcardSetCard.builder()
                .flashcardSet(set)
                .flashcard(card)
                .build();
        
        flashcardSetCardRepository.save(setCard);
        
        // Create progress
        FlashcardProgress progress = FlashcardProgress.builder()
                .studentId(userId)
                .flashcard(card)
                .easinessFactor(2.5f)
                .repetition(0)
                .intervalDays(0)
                .nextReviewDate(LocalDateTime.now())
                .learningState(LearningState.NEW)
                .build();
        progressRepository.save(progress);
        
        return ResponseEntity.ok(ApiResponse.success(200, "Word added successfully", null));
    }
}
