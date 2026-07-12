package com.tommy.catalog.application.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCourseRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String shortDescription;

    @NotBlank(message = "Course")
    private String level;

    @NotBlank(message = "Language cannot be empty")
    private String language;

    @NotNull(message = "Course price cannot be empty")
    private BigDecimal price;

}