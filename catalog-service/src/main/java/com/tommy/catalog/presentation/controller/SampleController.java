package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.port.in.GetSampleUseCase;
import com.tommy.catalog.domain.entity.SampleCatalog;
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
    public SampleCatalog getSampleById(@PathVariable Long id) {
        return getSampleUseCase.getSampleById(id);
    }
}
