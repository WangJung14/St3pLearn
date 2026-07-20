package com.tommy.learning.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SubmitExamRequest {
    @NotNull(message = "Answers list cannot be null")
    @Valid
    private List<QuestionAnswerRequest> answers;
}
