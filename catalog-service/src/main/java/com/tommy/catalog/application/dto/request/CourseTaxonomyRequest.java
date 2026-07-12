package com.tommy.catalog.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseTaxonomyRequest {
    @NotEmpty(message = "The course must belong to at least one category")
    private Set<UUID> categoryIds;

    private Set<UUID> tagIds; // Tags can be empty if teacher doesn't want to add
}
