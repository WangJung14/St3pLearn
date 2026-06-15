package com.tommy.catalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRequest {
    @NotBlank(message = "Category name cannot be empty")
    private String name;
}
