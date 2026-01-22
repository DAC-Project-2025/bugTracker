package com.user_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_service.enums.OutboxStatus;
import com.user_service.event.BaseEvent;
import com.user_service.models.Outbox;
import com.user_service.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {
	private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Save event to outbox (called within same transaction as business logic)
     */
    @Transactional
    public void saveEvent(BaseEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            
            Outbox outbox = Outbox.builder()
                    .aggregateId(event.getAggregateId())
                    .aggregateType(event.getAggregateType())
                    .eventType(event.getEventType())
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .build();
            
            outboxRepository.save(outbox);
            log.debug("Event saved to outbox: {} for aggregate: {}", 
                event.getEventType(), event.getAggregateId());
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: {}", event.getEventType(), e);
            throw new RuntimeException("Failed to save event to outbox", e);
        }
    }

    /**
     * Mark event as published
     */
    @Transactional
    public void markAsPublished(UUID outboxId) {
        outboxRepository.findById(outboxId).ifPresent(outbox -> {
            outbox.markAsPublished();
            outboxRepository.save(outbox);
            log.debug("Outbox event marked as published: {}", outboxId);
        });
    }

    /**
     * Mark event as failed
     */
    @Transactional
    public void markAsFailed(UUID outboxId, String errorMessage) {
        outboxRepository.findById(outboxId).ifPresent(outbox -> {
            outbox.markAsFailed(errorMessage);
            outboxRepository.save(outbox);
            log.warn("Outbox event marked as failed: {}. Error: {}", outboxId, errorMessage);
        });
    }
}
