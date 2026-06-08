package com.tommy.admin.infrastructure.persistence.repository;

import com.tommy.admin.application.port.out.SampleRepository;
import com.tommy.admin.domain.entity.SampleAdmin;
import com.tommy.admin.infrastructure.persistence.entity.SampleJpaEntity;
import com.tommy.admin.infrastructure.persistence.mapper.SampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SampleRepositoryImpl implements SampleRepository {

    private final SampleJpaRepository jpaRepository;
    private final SampleMapper mapper;

    @Override
    public SampleAdmin save(SampleAdmin sample) {
        SampleJpaEntity jpaEntity = mapper.toJpaEntity(sample);
        SampleJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SampleAdmin> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
