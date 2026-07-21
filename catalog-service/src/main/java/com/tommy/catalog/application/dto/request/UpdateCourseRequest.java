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
public class UpdateCourseRequest {

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String shortDescription;

    private String thumbnailUrl;

    @NotBlank(message = "Course level cannot be blank")
    private String level;

    @NotBlank(message = "Language cannot be blank")
    private String language;

    @NotNull(message = "Course price cannot be blank")
    private BigDecimal price;
}
