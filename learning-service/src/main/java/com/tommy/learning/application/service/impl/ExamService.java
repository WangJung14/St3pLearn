package com.tommy.learning.application.service.impl;

import com.tommy.common.exception.AppException;
import com.tommy.learning.application.dto.request.CreateExamRequest;
import com.tommy.learning.application.dto.request.UpdateExamQuestionsRequest;
import com.tommy.learning.application.dto.request.UpdateExamRequest;
import com.tommy.learning.application.dto.request.UpdateExamStatusRequest;
import com.tommy.learning.application.dto.response.ExamResponse;
import com.tommy.learning.application.dto.response.QuestionResponse;
import com.tommy.learning.application.service.IExamService;
import com.tommy.learning.domain.entity.Exam;
import com.tommy.learning.domain.entity.ExamQuestion;
import com.tommy.learning.domain.entity.Question;
import com.tommy.learning.domain.enums.ExamStatus;
import com.tommy.learning.infrastructure.persistence.repository.ExamQuestionRepository;
import com.tommy.learning.infrastructure.persistence.repository.ExamRepository;
import com.tommy.learning.infrastructure.persistence.repository.QuestionBankRepository;
import com.tommy.learning.infrastructure.persistence.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamService implements IExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionBankRepository questionBankRepository;

    @Override
    @Transactional
    public ExamResponse createExam(UUID instructorId, CreateExamRequest request) {
        log.info("Creating exam for course {} by instructor {}", request.getCourseId(), instructorId);

        Exam exam = Exam.builder()
                .courseId(request.getCourseId())
                .instructorId(instructorId)
                .title(request.getTitle())
                .durationMinutes(request.getDurationMinutes())
                .passingScore(request.getPassingScore())
                .maxAttempts(request.getMaxAttempts())
                .status(ExamStatus.DRAFT)
                .isDeleted(false)
                .build();

        Exam savedExam = examRepository.save(exam);

        saveExamQuestions(savedExam, request.getQuestionIds(), instructorId);

        return mapToResponse(savedExam);
    }

    @Override
    @Transactional
    public ExamResponse updateExamInfo(UUID instructorId, UUID examId, UpdateExamRequest request) {
        Exam exam = getExamAndVerifyOwnership(examId, instructorId);

        exam.setTitle(request.getTitle());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setPassingScore(request.getPassingScore());
        exam.setMaxAttempts(request.getMaxAttempts());

        Exam savedExam = examRepository.save(exam);
        return mapToResponse(savedExam);
    }

    @Override
    @Transactional
    public ExamResponse updateExamQuestions(UUID instructorId, UUID examId, UpdateExamQuestionsRequest request) {
        Exam exam = getExamAndVerifyOwnership(examId, instructorId);

        if (exam.getStatus() == ExamStatus.PUBLISHED) {
            throw new AppException(com.tommy.common.exception.ErrorCode.EXAM_INVALID_STATE);
        }

        examQuestionRepository.deleteByExamId(examId);
        saveExamQuestions(exam, request.getQuestionIds(), instructorId);

        return mapToResponse(exam);
    }

    @Override
    @Transactional
    public ExamResponse updateExamStatus(UUID instructorId, UUID examId, UpdateExamStatusRequest request) {
        Exam exam = getExamAndVerifyOwnership(examId, instructorId);
        exam.setStatus(request.getStatus());
        Exam savedExam = examRepository.save(exam);
        return mapToResponse(savedExam);
    }

    @Override
    @Transactional
    public void deleteExam(UUID instructorId, UUID examId) {
        Exam exam = getExamAndVerifyOwnership(examId, instructorId);

        if (checkIfExamHasAttempts(examId)) {
            // If it has attempts, we can only archive
            exam.setStatus(ExamStatus.ARCHIVED);
            examRepository.save(exam);
            log.info("Exam {} has attempts. Archived instead of deleting.", examId);
        } else {
            // Safe to soft delete
            exam.setDeleted(true);
            examRepository.save(exam);
            log.info("Exam {} softly deleted.", examId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsByInstructor(UUID instructorId) {
        return examRepository.findByInstructorIdAndIsDeletedFalse(instructorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponse getExamById(UUID instructorId, UUID examId) {
        Exam exam = getExamAndVerifyOwnership(examId, instructorId);
        return mapToResponse(exam);
    }

    private Exam getExamAndVerifyOwnership(UUID examId, UUID instructorId) {
        Exam exam = examRepository.findByIdAndIsDeletedFalse(examId)
                .orElseThrow(() -> new AppException(com.tommy.common.exception.ErrorCode.EXAM_NOT_FOUND));

        if (!exam.getInstructorId().equals(instructorId)) {
            throw new AppException(com.tommy.common.exception.ErrorCode.EXAM_ACCESS_DENIED);
        }
        return exam;
    }

    private void saveExamQuestions(Exam exam, List<UUID> questionIds, UUID instructorId) {
        if (questionIds == null || questionIds.isEmpty()) return;

        int order = 1;
        for (UUID qId : questionIds) {
            Question question = questionRepository.findByIdAndIsDeletedFalse(qId)
                    .orElseThrow(() -> new AppException(com.tommy.common.exception.ErrorCode.QUESTION_NOT_FOUND));

            // Verify question ownership
            var bank = questionBankRepository.findById(question.getBankId()).orElse(null);
            if (bank == null || !bank.getInstructorId().equals(instructorId)) {
                throw new AppException(com.tommy.common.exception.ErrorCode.QUESTION_ACCESS_DENIED);
            }

            ExamQuestion eq = ExamQuestion.builder()
                    .exam(exam)
                    .question(question)
                    .displayOrder(order++)
                    .build();
            examQuestionRepository.save(eq);
        }
    }

    private boolean checkIfExamHasAttempts(UUID examId) {
        // MOCK for Phase 7: Since we don't have exam_attempts yet, we'll return false.
        // In reality, this would query exam_attempts table.
        return false;
    }

    private ExamResponse mapToResponse(Exam exam) {
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdOrderByDisplayOrderAsc(exam.getId());

        double totalScore = 0.0;
        List<QuestionResponse> questionResponses = new ArrayList<>();

        for (ExamQuestion eq : examQuestions) {
            Question q = eq.getQuestion();
            totalScore += (q.getPoints() != null ? q.getPoints() : 0.0);

            questionResponses.add(QuestionResponse.builder()
                    .id(q.getId())
                    .bankId(q.getBankId())
                    .type(q.getType())
                    .content(q.getContent())
                    .metadata(q.getMetadata())
                    .difficulty(q.getDifficulty())
                    .points(q.getPoints())
                    .build());
        }

        return ExamResponse.builder()
                .id(exam.getId())
                .courseId(exam.getCourseId())
                .instructorId(exam.getInstructorId())
                .title(exam.getTitle())
                .durationMinutes(exam.getDurationMinutes())
                .passingScore(exam.getPassingScore())
                .totalScore(totalScore)
                .maxAttempts(exam.getMaxAttempts())
                .status(exam.getStatus())
                .questions(questionResponses)
                .build();
    }
}
