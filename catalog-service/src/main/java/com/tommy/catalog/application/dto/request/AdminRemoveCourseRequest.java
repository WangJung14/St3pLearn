package com.tommy.catalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRemoveCourseRequest {

    @NotBlank(message = "Reason is required when removing a course")
    private String reason;

}
