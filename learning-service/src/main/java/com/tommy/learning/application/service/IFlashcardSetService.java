package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.CreateFlashcardSetRequest;
import com.tommy.learning.application.dto.request.AddFlashcardToSetRequest;
import com.tommy.learning.application.dto.response.FlashcardSetSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface IFlashcardSetService {
    FlashcardSetSummaryResponse create(UUID instructorId, CreateFlashcardSetRequest request);
    FlashcardSetSummaryResponse cloneSet(UUID instructorId, UUID setId);
    FlashcardSetSummaryResponse addCard(UUID instructorId, UUID setId, AddFlashcardToSetRequest request);
    List<FlashcardSetSummaryResponse> getMySets(UUID instructorId);
    List<FlashcardSetSummaryResponse> getAvailableSets(UUID studentId);
    boolean canStudentAccess(UUID studentId, UUID setId);
}
