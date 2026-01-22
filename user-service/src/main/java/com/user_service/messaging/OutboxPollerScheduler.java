package com.user_service.messaging;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_service.enums.OutboxStatus;
import com.user_service.event.BaseEvent;
import com.user_service.models.Outbox;
import com.user_service.repository.OutboxRepository;
import com.user_service.service.OutboxService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPollerScheduler {
	 private final OutboxRepository outboxRepository;
	    private final OutboxService outboxService;
	    private final KafkaEventPublisher kafkaEventPublisher;
	    private final ObjectMapper objectMapper;

	    /**
	     * Poll and publish pending events every 5 seconds
	     */
	    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
	    @Transactional
	    public void pollAndPublishEvents() {
	        List<Outbox> pendingEvents = outboxRepository.findPendingEvents(
	            OutboxStatus.PENDING, 
	            PageRequest.of(0, 100)
	        );

	        if (pendingEvents.isEmpty()) {
	            return;
	        }

	        log.info("Processing {} pending outbox events", pendingEvents.size());

	        for (Outbox outbox : pendingEvents) {
	            try {
	                // Deserialize event
	                BaseEvent event = deserializeEvent(outbox);
	                
	                // Publish to Kafka
	                kafkaEventPublisher.publishEvent(event)
	                    .thenAccept(result -> {
	                        outboxService.markAsPublished(outbox.getId());
	                    })
	                    .exceptionally(ex -> {
	                        outboxService.markAsFailed(outbox.getId(), ex.getMessage());
	                        return null;
	                    });
	                
	            } catch (Exception e) {
	                log.error("Failed to process outbox event: {}", outbox.getId(), e);
	                outboxService.markAsFailed(outbox.getId(), e.getMessage());
	            }
	        }
	    }

	    /**
	     * Clean up old published events (run daily)
	     */
	    @Scheduled(cron = "0 0 2 * * *")  // 2 AM daily
	    @Transactional
	    public void cleanUpOldEvents() {
	        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
	        int deleted = outboxRepository.deleteOldPublishedEvents(threshold);
	        
	        if (deleted > 0) {
	            log.info("Cleaned up {} old outbox events", deleted);
	        }
	    }

	    /**
	     * Log outbox metrics (every minute)
	     */
	    @Scheduled(fixedRate = 60000)
	    public void logOutboxMetrics() {
	        long pending = outboxRepository.countByStatus(OutboxStatus.PENDING);
	        long failed = outboxRepository.countByStatus(OutboxStatus.FAILED);
	        
	        if (pending > 0 || failed > 0) {
	            log.info("Outbox metrics - Pending: {}, Failed: {}", pending, failed);
	        }
	    }

	    private BaseEvent deserializeEvent(Outbox outbox) throws Exception {
	        Class<? extends BaseEvent> eventClass = getEventClass(outbox.getEventType());
	        return objectMapper.readValue(outbox.getPayload(), eventClass);
	    }

	    private Class<? extends BaseEvent> getEventClass(String eventType) {
	        String packageName = "com.bugtracker.userservice.application.event.";
	        try {
	            return (Class<? extends BaseEvent>) Class.forName(packageName + eventType);
	        } catch (ClassNotFoundException e) {
	            throw new RuntimeException("Unknown event type: " + eventType, e);
	        }
	    }
}
