package com.tommy.identity.infrastructure.messaging;

import com.tommy.common.event.ModerationResolvedEvent;
import com.tommy.identity.domain.entity.Account;
import com.tommy.identity.domain.enums.AccountStatus;
import com.tommy.identity.infrastructure.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdentityEventListener {

    private final AccountRepository accountRepository;

    @RabbitListener(queues = "identity.moderation.events.queue")
    @Transactional
    public void handleModerationResolvedEvent(ModerationResolvedEvent event) {
        log.info("Identity received ModerationResolvedEvent: {}", event);

        if ("USER".equalsIgnoreCase(event.getTargetType())) {
            if ("SUSPEND_USER".equalsIgnoreCase(event.getAction())) {
                UUID userId = UUID.fromString(event.getTargetId());
                accountRepository.findById(userId).ifPresent(account -> {
                    account.setStatus(AccountStatus.SUSPENDED);
                    accountRepository.save(account);
                    log.info("Suspended user {} due to ModerationCase {}", userId, event.getCaseId());
                });
            } else if ("LOCK_USER".equalsIgnoreCase(event.getAction())) {
                UUID userId = UUID.fromString(event.getTargetId());
                accountRepository.findById(userId).ifPresent(account -> {
                    account.setStatus(AccountStatus.LOCKED);
                    accountRepository.save(account);
                    log.info("Locked user {} due to ModerationCase {}", userId, event.getCaseId());
                });
            }
        }
    }
}
