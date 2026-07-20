package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.CreateQuestionRequest;
import com.tommy.learning.application.dto.request.UpdateQuestionRequest;
import com.tommy.learning.application.dto.response.QuestionResponse;

import java.util.List;
import java.util.UUID;

public interface IQuestionService {
    QuestionResponse createQuestion(UUID instructorId, UUID bankId, CreateQuestionRequest request);
    QuestionResponse updateQuestion(UUID instructorId, UUID questionId, UpdateQuestionRequest request);
    void deleteQuestion(UUID instructorId, UUID questionId);
    List<QuestionResponse> getQuestionsByBankId(UUID instructorId, UUID bankId);
}
