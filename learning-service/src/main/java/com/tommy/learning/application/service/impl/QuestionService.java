package com.tommy.learning.application.service.impl;

import com.tommy.common.exception.AppException;
import com.tommy.learning.application.dto.request.CreateQuestionRequest;
import com.tommy.learning.application.dto.request.UpdateQuestionRequest;
import com.tommy.learning.application.dto.response.QuestionResponse;
import com.tommy.learning.application.service.IQuestionService;
import com.tommy.learning.domain.entity.Question;
import com.tommy.learning.domain.entity.QuestionBank;
import com.tommy.learning.domain.entity.json.Option;
import com.tommy.learning.domain.enums.ExamStatus;
import com.tommy.learning.domain.enums.QuestionType;
import com.tommy.learning.infrastructure.persistence.repository.ExamQuestionRepository;
import com.tommy.learning.infrastructure.persistence.repository.QuestionBankRepository;
import com.tommy.learning.infrastructure.persistence.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionBankRepository questionBankRepository;
    private final ExamQuestionRepository examQuestionRepository;

    @Override
    @Transactional
    public QuestionResponse createQuestion(UUID instructorId, UUID bankId, CreateQuestionRequest request) {
        log.info("Instructor {} is creating a question in bank {}", instructorId, bankId);

        verifyBankOwnership(bankId, instructorId);
        validateQuestionMetadata(request.getType(), request.getMetadata().getOptions());

        Question question = Question.builder()
                .bankId(bankId)
                .type(request.getType())
                .content(request.getContent())
                .metadata(request.getMetadata())
                .difficulty(request.getDifficulty())
                .points(request.getPoints())
                .isDeleted(false)
                .build();

        Question savedQuestion = questionRepository.save(question);
        return mapToResponse(savedQuestion);
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(UUID instructorId, UUID questionId, UpdateQuestionRequest request) {
        log.info("Instructor {} is updating question {}", instructorId, questionId);

        Question question = getQuestionAndVerifyOwnership(questionId, instructorId);
        
        // Mock check for Phase 7 (Exam usage)
        if (checkIfQuestionUsedInPublishedExam(questionId)) {
            throw new AppException(com.tommy.common.exception.ErrorCode.INVALID_QUESTION_DATA);
        }

        validateQuestionMetadata(question.getType(), request.getMetadata().getOptions());

        question.setContent(request.getContent());
        question.setMetadata(request.getMetadata());
        question.setDifficulty(request.getDifficulty());
        question.setPoints(request.getPoints());

        Question updatedQuestion = questionRepository.save(question);
        return mapToResponse(updatedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(UUID instructorId, UUID questionId) {
        log.info("Instructor {} is deleting question {}", instructorId, questionId);

        Question question = getQuestionAndVerifyOwnership(questionId, instructorId);
        question.setDeleted(true);
        questionRepository.save(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByBankId(UUID instructorId, UUID bankId) {
        log.info("Instructor {} is fetching questions for bank {}", instructorId, bankId);
        
        verifyBankOwnership(bankId, instructorId);

        List<Question> questions = questionRepository.findByBankIdAndIsDeletedFalse(bankId);
        return questions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private void verifyBankOwnership(UUID bankId, UUID instructorId) {
        QuestionBank bank = questionBankRepository.findByIdAndIsDeletedFalse(bankId)
                .orElseThrow(() -> new AppException(com.tommy.common.exception.ErrorCode.QUESTION_BANK_NOT_FOUND));

        if (!bank.getInstructorId().equals(instructorId)) {
            throw new AppException(com.tommy.common.exception.ErrorCode.QUESTION_BANK_ACCESS_DENIED);
        }
    }

    private Question getQuestionAndVerifyOwnership(UUID questionId, UUID instructorId) {
        // Will use AppException(ErrorCode.QUESTION_NOT_FOUND) after adding it.
        // For now, using custom error code integer to avoid compilation issues until common-library is updated.
        Question question = questionRepository.findByIdAndIsDeletedFalse(questionId)
                .orElseThrow(() -> new AppException(com.tommy.common.exception.ErrorCode.QUESTION_NOT_FOUND));

        verifyBankOwnership(question.getBankId(), instructorId);
        return question;
    }

    private void validateQuestionMetadata(QuestionType type, List<Option> options) {
        if (type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE) {
            if (CollectionUtils.isEmpty(options)) {
                throw new AppException(com.tommy.common.exception.ErrorCode.INVALID_QUESTION_DATA);
            }
            boolean hasCorrectAnswer = options.stream().anyMatch(Option::isCorrect);
            if (!hasCorrectAnswer) {
                throw new AppException(com.tommy.common.exception.ErrorCode.INVALID_QUESTION_DATA);
            }
        }
    }

    private boolean checkIfQuestionUsedInPublishedExam(UUID questionId) {
        return examQuestionRepository.existsByQuestionIdAndExam_Status(questionId, ExamStatus.PUBLISHED);
    }

    private QuestionResponse mapToResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .bankId(question.getBankId())
                .type(question.getType())
                .content(question.getContent())
                .metadata(question.getMetadata())
                .difficulty(question.getDifficulty())
                .points(question.getPoints())
                .build();
    }
}
