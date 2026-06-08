package com.tommy.payment.presentation.controller;

import com.tommy.payment.application.port.in.GetSampleUseCase;
import com.tommy.payment.domain.entity.SamplePayment;
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
    public SamplePayment getSampleById(@PathVariable Long id) {
        return getSampleUseCase.getSampleById(id);
    }
}
