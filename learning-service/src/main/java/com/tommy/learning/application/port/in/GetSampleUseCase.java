package com.tommy.learning.application.port.in;

import com.tommy.learning.domain.entity.SampleLearning;

public interface GetSampleUseCase {
    SampleLearning getSampleById(Long id);
}
