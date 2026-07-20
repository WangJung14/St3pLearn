package com.tommy.learning.domain.entity.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {
    private List<String> selectedOptionIds;
    private String textAnswer;
    private String audioUrl;
}
