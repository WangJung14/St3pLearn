package com.tommy.catalog.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitReviewRequest {

    @NotNull(message = "Rating star cannot be blank")
    @Min(value = 1, message = "The lowest rating is 1 star")
    @Max(value = 5, message = "The highest rating is 5 stars")
    private Integer rating;

    @NotBlank(message = "Review text cannot be blank")
    private String reviewText;
}