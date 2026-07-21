package com.tommy.learning.application.service.impl;

import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.learning.application.dto.request.CreateFlashcardSetRequest;
import com.tommy.learning.application.dto.request.AddFlashcardToSetRequest;
import com.tommy.learning.application.dto.response.FlashcardSetSummaryResponse;
import com.tommy.learning.application.service.IFlashcardSetService;
import com.tommy.learning.domain.entity.flashcard.FlashcardSet;
import com.tommy.learning.domain.entity.flashcard.FlashcardSetCard;
import com.tommy.learning.domain.entity.flashcard.Flashcard;
import com.tommy.learning.domain.entity.vocabulary.Vocabulary;
import com.tommy.learning.domain.entity.vocabulary.VocabularyMeaning;
import com.tommy.learning.domain.enums.BackType;
import com.tommy.learning.domain.enums.FrontType;
import com.tommy.learning.domain.enums.SourceEnum;
import com.tommy.learning.domain.enums.VisibilityEnum;
import com.tommy.learning.infrastructure.persistence.repository.FlashcardSetCardRepository;
import com.tommy.learning.infrastructure.persistence.repository.FlashcardSetRepository;
import com.tommy.learning.infrastructure.persistence.repository.FlashcardRepository;
import com.tommy.learning.infrastructure.persistence.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashcardSetService implements IFlashcardSetService {
    private final FlashcardSetRepository setRepository;
    private final FlashcardSetCardRepository setCardRepository;
    private final FlashcardRepository flashcardRepository;
    private final VocabularyRepository vocabularyRepository;

    @Override
    @Transactional
    public FlashcardSetSummaryResponse create(UUID instructorId, CreateFlashcardSetRequest request) {
        FlashcardSet set = FlashcardSet.builder()
                .title(request.getTitle().trim())
                .courseId(request.getCourseId())
                .instructorId(instructorId)
                .visibility(request.getVisibility())
                .build();
        return toSummary(setRepository.save(set));
    }

    @Override
    @Transactional
    public FlashcardSetSummaryResponse cloneSet(UUID instructorId, UUID setId) {
        FlashcardSet source = setRepository.findByIdAndDeletedAtIsNull(setId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_NOT_FOUND));
        boolean canClone = source.getInstructorId().equals(instructorId)
                || source.getVisibility() == VisibilityEnum.PUBLIC
                || source.getVisibility() == VisibilityEnum.SYSTEM;
        if (!canClone) throw new AppException(ErrorCode.COURSE_ACCESS_DENIED);

        FlashcardSet clone = setRepository.save(FlashcardSet.builder()
                .title(source.getTitle() + " (Copy)")
                .courseId(source.getCourseId())
                .instructorId(instructorId)
                .visibility(VisibilityEnum.PRIVATE)
                .build());

        List<FlashcardSetCard> clonedCards = setCardRepository.findByFlashcardSetIdOrderByDisplayOrderAsc(source.getId())
                .stream()
                .map(sourceCard -> FlashcardSetCard.builder()
                        .flashcardSet(clone)
                        .flashcard(sourceCard.getFlashcard())
                        .displayOrder(sourceCard.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());
        setCardRepository.saveAll(clonedCards);
        return toSummary(clone);
    }

    @Override
    @Transactional
    public FlashcardSetSummaryResponse addCard(UUID instructorId, UUID setId, AddFlashcardToSetRequest request) {
        FlashcardSet set = setRepository.findByIdAndDeletedAtIsNull(setId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_NOT_FOUND));
        if (!set.getInstructorId().equals(instructorId)) {
            throw new AppException(ErrorCode.COURSE_ACCESS_DENIED);
        }

        Vocabulary vocabulary = Vocabulary.builder()
                .lemma(request.getLemma().trim())
                .language(request.getLanguage() == null || request.getLanguage().isBlank() ? "EN" : request.getLanguage().trim().toUpperCase())
                .phonetic(request.getPhonetic())
                .partOfSpeech(request.getPartOfSpeech())
                .cefrLevel(request.getCefrLevel())
                .source(SourceEnum.USER_IMPORT)
                .visibility(VisibilityEnum.PRIVATE)
                .build();
        vocabulary.getMeanings().add(VocabularyMeaning.builder()
                .vocabulary(vocabulary)
                .definition(request.getDefinition().trim())
                .build());
        vocabulary = vocabularyRepository.save(vocabulary);

        Flashcard flashcard = flashcardRepository.save(Flashcard.builder()
                .vocabulary(vocabulary)
                .frontType(FrontType.WORD)
                .backType(BackType.MEANING)
                .createdBy(instructorId)
                .build());

        int displayOrder = (int) setCardRepository.countByFlashcardSetId(setId);
        setCardRepository.save(FlashcardSetCard.builder()
                .flashcardSet(set)
                .flashcard(flashcard)
                .displayOrder(displayOrder)
                .build());
        return toSummary(set);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardSetSummaryResponse> getMySets(UUID instructorId) {
        return setRepository.findByInstructorIdAndDeletedAtIsNullOrderByCreatedAtDesc(instructorId)
                .stream().map(this::toSummary).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardSetSummaryResponse> getAvailableSets(UUID studentId) {
        return setRepository.findAvailableForStudent(studentId)
                .stream().map(this::toSummary).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canStudentAccess(UUID studentId, UUID setId) {
        return setRepository.existsAvailableForStudent(studentId, setId);
    }

    private FlashcardSetSummaryResponse toSummary(FlashcardSet set) {
        return FlashcardSetSummaryResponse.builder()
                .id(set.getId())
                .title(set.getTitle())
                .courseId(set.getCourseId())
                .instructorId(set.getInstructorId())
                .visibility(set.getVisibility())
                .cardCount(setCardRepository.countByFlashcardSetId(set.getId()))
                .createdAt(set.getCreatedAt())
                .updatedAt(set.getUpdatedAt())
                .build();
    }
}
