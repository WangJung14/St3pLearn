package com.tommy.learning.application.dto.request;

import com.tommy.learning.domain.enums.ExamStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateExamStatusRequest {
    @NotNull(message = "Status cannot be null")
    private ExamStatus status;
}
