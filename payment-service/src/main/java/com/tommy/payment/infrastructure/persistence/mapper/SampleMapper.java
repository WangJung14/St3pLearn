package com.tommy.payment.infrastructure.persistence.mapper;

import com.tommy.payment.domain.entity.SamplePayment;
import com.tommy.payment.infrastructure.persistence.entity.SampleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SampleMapper {

    public SamplePayment toDomain(SampleJpaEntity entity) {
        if (entity == null) return null;
        return SamplePayment.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public SampleJpaEntity toJpaEntity(SamplePayment domain) {
        if (domain == null) return null;
        return SampleJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
