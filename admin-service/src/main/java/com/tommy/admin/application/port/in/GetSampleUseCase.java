package com.tommy.admin.application.port.in;

import com.tommy.admin.domain.entity.SampleAdmin;

public interface GetSampleUseCase {
    SampleAdmin getSampleById(Long id);
}
