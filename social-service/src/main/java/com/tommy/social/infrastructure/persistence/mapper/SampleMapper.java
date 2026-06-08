package com.tommy.social.infrastructure.persistence.mapper;

import com.tommy.social.domain.entity.SampleSocial;
import com.tommy.social.infrastructure.persistence.entity.SampleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SampleMapper {

    public SampleSocial toDomain(SampleJpaEntity entity) {
        if (entity == null) return null;
        return SampleSocial.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public SampleJpaEntity toJpaEntity(SampleSocial domain) {
        if (domain == null) return null;
        return SampleJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
