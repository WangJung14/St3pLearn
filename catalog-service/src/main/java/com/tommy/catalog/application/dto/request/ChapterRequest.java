package com.tommy.catalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChapterRequest {
    @NotBlank(message = "Chapter titles must not be blank")
    private String title;
}
