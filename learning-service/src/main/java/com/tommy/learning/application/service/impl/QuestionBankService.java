package com.tommy.learning.application.service.impl;

import com.tommy.common.exception.AppException;
import com.tommy.learning.application.dto.request.CreateQuestionBankRequest;
import com.tommy.learning.application.dto.request.UpdateQuestionBankRequest;
import com.tommy.learning.application.dto.response.QuestionBankResponse;
import com.tommy.learning.application.service.IQuestionBankService;
import com.tommy.learning.domain.entity.QuestionBank;
import com.tommy.learning.infrastructure.persistence.repository.QuestionBankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBankService implements IQuestionBankService {

    private final QuestionBankRepository questionBankRepository;

    @Override
    @Transactional
    public QuestionBankResponse createBank(UUID instructorId, CreateQuestionBankRequest request) {
        log.info("Instructor {} is creating question bank: {}", instructorId, request.getTitle());

        QuestionBank bank = QuestionBank.builder()
                .courseId(request.getCourseId())
                .instructorId(instructorId)
                .title(request.getTitle())
                .description(request.getDescription())
                .isDeleted(false)
                .build();

        QuestionBank savedBank = questionBankRepository.save(bank);
        return mapToResponse(savedBank);
    }

    @Override
    @Transactional
    public QuestionBankResponse updateBank(UUID instructorId, UUID bankId, UpdateQuestionBankRequest request) {
        log.info("Instructor {} is updating question bank {}", instructorId, bankId);

        QuestionBank bank = getBankAndVerifyOwner(bankId, instructorId);

        bank.setTitle(request.getTitle());
        bank.setDescription(request.getDescription());

        QuestionBank updatedBank = questionBankRepository.save(bank);
        return mapToResponse(updatedBank);
    }

    @Override
    @Transactional
    public void deleteBank(UUID instructorId, UUID bankId) {
        log.info("Instructor {} is deleting question bank {}", instructorId, bankId);

        QuestionBank bank = getBankAndVerifyOwner(bankId, instructorId);
        bank.setDeleted(true);
        questionBankRepository.save(bank);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionBankResponse> getMyBanks(UUID instructorId) {
        log.info("Fetching question banks for instructor {}", instructorId);
        List<QuestionBank> banks = questionBankRepository.findByInstructorIdAndIsDeletedFalse(instructorId);
        return banks.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private QuestionBank getBankAndVerifyOwner(UUID bankId, UUID instructorId) {
        QuestionBank bank = questionBankRepository.findByIdAndIsDeletedFalse(bankId)
                .orElseThrow(() -> new AppException(com.tommy.common.exception.ErrorCode.QUESTION_BANK_NOT_FOUND));

        if (!bank.getInstructorId().equals(instructorId)) {
            throw new AppException(com.tommy.common.exception.ErrorCode.QUESTION_BANK_ACCESS_DENIED);
        }
        return bank;
    }

    private QuestionBankResponse mapToResponse(QuestionBank bank) {
        return QuestionBankResponse.builder()
                .id(bank.getId())
                .courseId(bank.getCourseId())
                .title(bank.getTitle())
                .description(bank.getDescription())
                .instructorId(bank.getInstructorId())
                .createdAt(bank.getCreatedAt())
                .updatedAt(bank.getUpdatedAt())
                .build();
    }
}
