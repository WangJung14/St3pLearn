package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import com.tommy.learning.application.dto.request.CreateExamRequest;
import com.tommy.learning.application.dto.request.UpdateExamQuestionsRequest;
import com.tommy.learning.application.dto.request.UpdateExamRequest;
import com.tommy.learning.application.dto.request.UpdateExamStatusRequest;
import com.tommy.learning.application.dto.request.GradeSubmissionRequest;
import com.tommy.learning.application.dto.request.SubmitExamRequest;
import com.tommy.learning.application.dto.response.ExamAttemptResponse;
import com.tommy.learning.application.dto.response.ExamResponse;
import com.tommy.learning.application.dto.response.ExamResultResponse;
import com.tommy.learning.application.dto.response.StartExamResponse;
import com.tommy.learning.application.service.IExamService;
import com.tommy.learning.domain.enums.ExamAttemptStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
@Slf4j
public class ExamController {

    private final IExamService examService;

    @PostMapping("/exams")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(
            @RequestHeader("X-User-Id") UUID instructorId,
            @Valid @RequestBody CreateExamRequest request) {

        log.info("Instructor {} creating exam for course {}", instructorId, request.getCourseId());
        ExamResponse response = examService.createExam(instructorId, request);
        return ResponseEntity.ok(ApiResponse.success(201, "Exam created successfully", response));
    }

    @PutMapping("/exams/{examId}")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<ExamResponse>> updateExamInfo(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID examId,
            @Valid @RequestBody UpdateExamRequest request) {

        log.info("Instructor {} updating exam {}", instructorId, examId);
        ExamResponse response = examService.updateExamInfo(instructorId, examId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Exam info updated successfully", response));
    }

    @PutMapping("/exams/{examId}/questions")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<ExamResponse>> updateExamQuestions(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID examId,
            @Valid @RequestBody UpdateExamQuestionsRequest request) {

        log.info("Instructor {} updating questions for exam {}", instructorId, examId);
        ExamResponse response = examService.updateExamQuestions(instructorId, examId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Exam questions updated successfully", response));
    }

    @PutMapping("/exams/{examId}/status")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<ExamResponse>> updateExamStatus(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID examId,
            @Valid @RequestBody UpdateExamStatusRequest request) {

        log.info("Instructor {} updating status for exam {}", instructorId, examId);
        ExamResponse response = examService.updateExamStatus(instructorId, examId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Exam status updated successfully", response));
    }

    @DeleteMapping("/exams/{examId}")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<Void>> deleteExam(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID examId) {

        log.info("Instructor {} deleting/archiving exam {}", instructorId, examId);
        examService.deleteExam(instructorId, examId);
        return ResponseEntity.ok(ApiResponse.success(200, "Exam deleted or archived successfully", null));
    }

    @GetMapping("/exams")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getExamsByInstructor(
            @RequestHeader("X-User-Id") UUID instructorId) {

        log.info("Fetching exams for instructor {}", instructorId);
        List<ExamResponse> response = examService.getExamsByInstructor(instructorId);
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched exams successfully", response));
    }

    @GetMapping("/exams/{examId}")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<ExamResponse>> getExamById(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID examId) {

        log.info("Fetching exam {} for instructor {}", examId, instructorId);
        ExamResponse response = examService.getExamById(instructorId, examId);
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched exam successfully", response));
    }

    @PostMapping("/exams/{examId}/attempts")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<StartExamResponse>> startExam(
            @RequestHeader("X-User-Id") UUID studentId,
            @PathVariable UUID examId) {

        log.info("Student {} starting exam {}", studentId, examId);
        StartExamResponse response = examService.startExam(studentId, examId);
        return ResponseEntity.ok(ApiResponse.success(201, "Exam started successfully", response));
    }

    @PostMapping("/exams/attempts/{attemptId}/submit")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<Void>> submitExam(
            @RequestHeader("X-User-Id") UUID studentId,
            @PathVariable UUID attemptId,
            @Valid @RequestBody SubmitExamRequest request) {

        log.info("Student {} submitting exam attempt {}", studentId, attemptId);
        examService.submitExam(studentId, attemptId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Exam submitted successfully", null));
    }

    @GetMapping("/exams/{examId}/submissions")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<Page<ExamAttemptResponse>>> getExamSubmissions(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID examId,
            @RequestParam(required = false) ExamAttemptStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Instructor {} fetching submissions for exam {}", instructorId, examId);
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamAttemptResponse> response = examService.getExamSubmissions(instructorId, examId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched exam submissions successfully", response));
    }

    @PutMapping("/exams/submissions/{attemptId}/grade")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<Void>> gradeSubmission(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID attemptId,
            @Valid @RequestBody GradeSubmissionRequest request) {

        log.info("Instructor {} grading submission for attempt {}", instructorId, attemptId);
        examService.gradeSubmission(instructorId, attemptId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Exam graded successfully", null));
    }

    @GetMapping("/exams/attempts/{attemptId}/result")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<ExamResultResponse>> getExamResult(
            @RequestHeader("X-User-Id") UUID studentId,
            @PathVariable UUID attemptId) {

        log.info("Student {} fetching exam result for attempt {}", studentId, attemptId);
        ExamResultResponse response = examService.getExamResult(studentId, attemptId);
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched exam result successfully", response));
    }

    @GetMapping("/courses/{courseId}/exams")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getExamsByCourse(
            @PathVariable UUID courseId) {
        log.info("Fetching exams for course {}", courseId);
        List<ExamResponse> response = examService.getExamsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched exams successfully", response));
    }
}
