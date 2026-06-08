package com.tommy.catalog.infrastructure.persistence.mapper;

import com.tommy.catalog.domain.entity.SampleCatalog;
import com.tommy.catalog.infrastructure.persistence.entity.SampleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SampleMapper {

    public SampleCatalog toDomain(SampleJpaEntity entity) {
        if (entity == null) return null;
        return SampleCatalog.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public SampleJpaEntity toJpaEntity(SampleCatalog domain) {
        if (domain == null) return null;
        return SampleJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
