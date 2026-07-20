package com.tommy.admin.application.service;

import com.tommy.admin.domain.entity.SystemConfig;
import com.tommy.admin.infrastructure.persistence.repository.SystemConfigRepository;
import com.tommy.common.event.SystemConfigUpdatedEvent;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional(readOnly = true)
    public List<SystemConfig> getAllConfigs() {
        return systemConfigRepository.findAll();
    }

    @Transactional
    public SystemConfig updateConfig(String key, String value, String description, String adminId) {
        SystemConfig config = systemConfigRepository.findById(key)
                .orElse(SystemConfig.builder()
                        .configKey(key)
                        .description(description)
                        .build());
        
        config.setConfigValue(value);
        config.setLastUpdatedBy(adminId);
        
        config = systemConfigRepository.save(config);

        SystemConfigUpdatedEvent event = SystemConfigUpdatedEvent.builder()
                .configKey(config.getConfigKey())
                .configValue(config.getConfigValue())
                .description(config.getDescription())
                .build();
        
        rabbitTemplate.convertAndSend("admin.events.exchange", "config.updated", event);
        log.info("Emitted SystemConfigUpdatedEvent for config {}", key);

        return config;
    }
}
