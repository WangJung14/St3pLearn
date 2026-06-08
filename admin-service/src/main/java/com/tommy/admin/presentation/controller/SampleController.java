package com.tommy.admin.presentation.controller;

import com.tommy.admin.application.port.in.GetSampleUseCase;
import com.tommy.admin.domain.entity.SampleAdmin;
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
    public SampleAdmin getSampleById(@PathVariable Long id) {
        return getSampleUseCase.getSampleById(id);
    }
}
