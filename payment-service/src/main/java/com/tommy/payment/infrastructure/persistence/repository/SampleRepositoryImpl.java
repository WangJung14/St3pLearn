package com.tommy.payment.infrastructure.persistence.repository;

import com.tommy.payment.application.port.out.SampleRepository;
import com.tommy.payment.domain.entity.SamplePayment;
import com.tommy.payment.infrastructure.persistence.entity.SampleJpaEntity;
import com.tommy.payment.infrastructure.persistence.mapper.SampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SampleRepositoryImpl implements SampleRepository {

    private final SampleJpaRepository jpaRepository;
    private final SampleMapper mapper;

    @Override
    public SamplePayment save(SamplePayment sample) {
        SampleJpaEntity jpaEntity = mapper.toJpaEntity(sample);
        SampleJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SamplePayment> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
