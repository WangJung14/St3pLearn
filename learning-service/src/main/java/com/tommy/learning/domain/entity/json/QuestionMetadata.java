package com.tommy.learning.domain.entity.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionMetadata {
    @Builder.Default
    private List<Option> options = new ArrayList<>();
    
    private String explanation;
    private String audioUrl;
}
