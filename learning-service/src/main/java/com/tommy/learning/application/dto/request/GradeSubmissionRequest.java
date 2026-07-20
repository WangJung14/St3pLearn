package com.tommy.learning.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GradeSubmissionRequest {
    @NotNull(message = "Grades list cannot be null")
    @Valid
    private List<QuestionGradeRequest> grades;
}
