package com.tommy.social.infrastructure.persistence.repository;

import com.tommy.social.application.port.out.SampleRepository;
import com.tommy.social.domain.entity.SampleSocial;
import com.tommy.social.infrastructure.persistence.entity.SampleJpaEntity;
import com.tommy.social.infrastructure.persistence.mapper.SampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SampleRepositoryImpl implements SampleRepository {

    private final SampleJpaRepository jpaRepository;
    private final SampleMapper mapper;

    @Override
    public SampleSocial save(SampleSocial sample) {
        SampleJpaEntity jpaEntity = mapper.toJpaEntity(sample);
        SampleJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SampleSocial> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
