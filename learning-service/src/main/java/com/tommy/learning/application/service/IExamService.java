package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.CreateExamRequest;
import com.tommy.learning.application.dto.request.UpdateExamQuestionsRequest;
import com.tommy.learning.application.dto.request.UpdateExamRequest;
import com.tommy.learning.application.dto.request.UpdateExamStatusRequest;
import com.tommy.learning.application.dto.response.ExamResponse;

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
}
