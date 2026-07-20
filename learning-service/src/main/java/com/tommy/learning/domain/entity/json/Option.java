package com.tommy.learning.domain.entity.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {
    private String id; // E.g., A, B, C, D
    private String text;
    private boolean isCorrect;
}
