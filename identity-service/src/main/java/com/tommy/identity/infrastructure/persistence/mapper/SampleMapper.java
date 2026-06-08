package com.tommy.identity.infrastructure.persistence.mapper;

import com.tommy.identity.domain.entity.SampleIdentity;
import com.tommy.identity.infrastructure.persistence.entity.SampleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SampleMapper {

    public SampleIdentity toDomain(SampleJpaEntity entity) {
        if (entity == null) return null;
        return SampleIdentity.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public SampleJpaEntity toJpaEntity(SampleIdentity domain) {
        if (domain == null) return null;
        return SampleJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
