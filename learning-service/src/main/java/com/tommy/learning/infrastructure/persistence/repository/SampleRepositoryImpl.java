package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.application.port.out.SampleRepository;
import com.tommy.learning.domain.entity.SampleLearning;
import com.tommy.learning.infrastructure.persistence.entity.SampleJpaEntity;
import com.tommy.learning.infrastructure.persistence.mapper.SampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SampleRepositoryImpl implements SampleRepository {

    private final SampleJpaRepository jpaRepository;
    private final SampleMapper mapper;

    @Override
    public SampleLearning save(SampleLearning sample) {
        SampleJpaEntity jpaEntity = mapper.toJpaEntity(sample);
        SampleJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SampleLearning> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
