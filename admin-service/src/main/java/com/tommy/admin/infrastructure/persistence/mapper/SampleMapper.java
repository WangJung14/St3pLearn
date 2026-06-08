package com.tommy.admin.infrastructure.persistence.mapper;

import com.tommy.admin.domain.entity.SampleAdmin;
import com.tommy.admin.infrastructure.persistence.entity.SampleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SampleMapper {

    public SampleAdmin toDomain(SampleJpaEntity entity) {
        if (entity == null) return null;
        return SampleAdmin.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public SampleJpaEntity toJpaEntity(SampleAdmin domain) {
        if (domain == null) return null;
        return SampleJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
