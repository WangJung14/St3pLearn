package com.tommy.social.application.port.in;

import com.tommy.social.domain.entity.SampleSocial;

public interface GetSampleUseCase {
    SampleSocial getSampleById(Long id);
}
