package com.tommy.learning.presentation.controller;

import com.tommy.learning.application.port.in.GetSampleUseCase;
import com.tommy.learning.domain.entity.SampleLearning;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/samples")
@RequiredArgsConstructor
public class SampleController {

    private final GetSampleUseCase getSampleUseCase;

    @GetMapping("/{id}")
    public SampleLearning getSampleById(@PathVariable Long id) {
        return getSampleUseCase.getSampleById(id);
    }
}
