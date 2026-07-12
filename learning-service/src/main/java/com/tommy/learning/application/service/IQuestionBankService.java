package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.CreateQuestionBankRequest;
import com.tommy.learning.application.dto.request.UpdateQuestionBankRequest;
import com.tommy.learning.application.dto.response.QuestionBankResponse;

import java.util.List;
import java.util.UUID;

public interface IQuestionBankService {
    QuestionBankResponse createBank(UUID instructorId, CreateQuestionBankRequest request);
    QuestionBankResponse updateBank(UUID instructorId, UUID bankId, UpdateQuestionBankRequest request);
    void deleteBank(UUID instructorId, UUID bankId);
    List<QuestionBankResponse> getMyBanks(UUID instructorId);
}
