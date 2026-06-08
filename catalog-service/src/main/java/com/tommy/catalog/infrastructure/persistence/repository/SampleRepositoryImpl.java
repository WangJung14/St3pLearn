package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.application.port.out.SampleRepository;
import com.tommy.catalog.domain.entity.SampleCatalog;
import com.tommy.catalog.infrastructure.persistence.entity.SampleJpaEntity;
import com.tommy.catalog.infrastructure.persistence.mapper.SampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SampleRepositoryImpl implements SampleRepository {

    private final SampleJpaRepository jpaRepository;
    private final SampleMapper mapper;

    @Override
    public SampleCatalog save(SampleCatalog sample) {
        SampleJpaEntity jpaEntity = mapper.toJpaEntity(sample);
        SampleJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SampleCatalog> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
