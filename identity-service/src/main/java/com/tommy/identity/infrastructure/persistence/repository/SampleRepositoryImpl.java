package com.tommy.identity.infrastructure.persistence.repository;

import com.tommy.identity.application.port.out.SampleRepository;
import com.tommy.identity.domain.entity.SampleIdentity;
import com.tommy.identity.infrastructure.persistence.entity.SampleJpaEntity;
import com.tommy.identity.infrastructure.persistence.mapper.SampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SampleRepositoryImpl implements SampleRepository {

    private final SampleJpaRepository jpaRepository;
    private final SampleMapper mapper;

    @Override
    public SampleIdentity save(SampleIdentity sample) {
        SampleJpaEntity jpaEntity = mapper.toJpaEntity(sample);
        SampleJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SampleIdentity> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
