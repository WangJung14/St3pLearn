package com.tommy.identity.application.port.in;

import com.tommy.identity.domain.entity.SampleIdentity;

public interface GetSampleUseCase {
    SampleIdentity getSampleById(Long id);
}
