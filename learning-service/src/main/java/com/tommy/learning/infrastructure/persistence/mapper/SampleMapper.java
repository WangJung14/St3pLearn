package com.tommy.learning.infrastructure.persistence.mapper;

import com.tommy.learning.domain.entity.SampleLearning;
import com.tommy.learning.infrastructure.persistence.entity.SampleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SampleMapper {

    public SampleLearning toDomain(SampleJpaEntity entity) {
        if (entity == null) return null;
        return SampleLearning.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public SampleJpaEntity toJpaEntity(SampleLearning domain) {
        if (domain == null) return null;
        return SampleJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
