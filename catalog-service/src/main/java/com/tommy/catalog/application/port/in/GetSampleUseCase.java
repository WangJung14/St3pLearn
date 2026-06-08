package com.tommy.catalog.application.port.in;

import com.tommy.catalog.domain.entity.SampleCatalog;

public interface GetSampleUseCase {
    SampleCatalog getSampleById(Long id);
}
