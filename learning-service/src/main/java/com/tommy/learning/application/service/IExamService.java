package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.CreateExamRequest;
import com.tommy.learning.application.dto.request.SubmitExamRequest;
import com.tommy.learning.application.dto.request.GradeSubmissionRequest;
import com.tommy.learning.application.dto.request.SubmitExamRequest;
import com.tommy.learning.application.dto.request.UpdateExamQuestionsRequest;
import com.tommy.learning.application.dto.request.UpdateExamRequest;
import com.tommy.learning.application.dto.request.UpdateExamStatusRequest;
import com.tommy.learning.application.dto.response.ExamAttemptResponse;
import com.tommy.learning.application.dto.response.ExamResponse;
import com.tommy.learning.application.dto.response.ExamResultResponse;
import com.tommy.learning.application.dto.response.StudentExamSummaryResponse;
import com.tommy.learning.domain.enums.ExamAttemptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tommy.learning.application.dto.response.StartExamResponse;

import java.util.List;
import java.util.UUID;

public interface IExamService {
    ExamResponse createExam(UUID instructorId, CreateExamRequest request);
    ExamResponse updateExamInfo(UUID instructorId, UUID examId, UpdateExamRequest request);
    ExamResponse updateExamQuestions(UUID instructorId, UUID examId, UpdateExamQuestionsRequest request);
    ExamResponse updateExamStatus(UUID instructorId, UUID examId, UpdateExamStatusRequest request);
    void deleteExam(UUID instructorId, UUID examId);
    List<ExamResponse> getExamsByInstructor(UUID instructorId);
    ExamResponse getExamById(UUID instructorId, UUID examId);
    List<StudentExamSummaryResponse> getAvailableExamsForStudent(UUID studentId);
    StartExamResponse startExam(UUID studentId, UUID examId);
    void submitExam(UUID studentId, UUID attemptId, SubmitExamRequest request);
    Page<ExamAttemptResponse> getExamSubmissions(UUID instructorId, UUID examId, ExamAttemptStatus status, Pageable pageable);
    void gradeSubmission(UUID instructorId, UUID attemptId, GradeSubmissionRequest request);
    ExamResultResponse getExamResult(UUID studentId, UUID attemptId);
}
