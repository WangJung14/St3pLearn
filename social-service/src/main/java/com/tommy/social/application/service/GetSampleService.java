package com.tommy.social.application.service;

import com.tommy.social.application.port.in.GetSampleUseCase;
import com.tommy.social.application.port.out.SampleRepository;
import com.tommy.social.domain.entity.SampleSocial;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSampleService implements GetSampleUseCase {

    private final SampleRepository sampleRepository;

    @Override
    public SampleSocial getSampleById(Long id) {
        return sampleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sample not found with id: " + id));
    }
}
