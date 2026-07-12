package com.tommy.catalog.application.dto.request;


import com.tommy.catalog.domain.enums.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LessonRequest {
    @NotBlank(message = "Lesson title cannot be empty")
    private String title;
    @NotNull(message = "Lesson type cannot be empty")
    private LessonType lessonType;
    private Integer durationSeconds;
    private Boolean isPreview;
}
