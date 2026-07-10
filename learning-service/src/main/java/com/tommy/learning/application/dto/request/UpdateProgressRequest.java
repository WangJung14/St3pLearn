package com.tommy.learning.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProgressRequest {
    
    @NotNull(message = "currentSeconds cannot be null")
    @Min(value = 0, message = "currentSeconds must be greater than or equal to 0")
    private Integer currentSeconds;

}
