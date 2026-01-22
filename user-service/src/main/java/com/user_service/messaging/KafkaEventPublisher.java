package com.user_service.messaging;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_service.event.BaseEvent;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {
	private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.user-events}")
    private String userEventsTopic;

    /**
     * Publish event to Kafka with circuit breaker and retry
     */
    @CircuitBreaker(name = "kafka", fallbackMethod = "publishFallback")
    @Retry(name = "kafka")
    public CompletableFuture<SendResult<String, Object>> publishEvent(BaseEvent event) {
        String topic = determineTopicByEventType(event.getEventType());
        String key = event.getAggregateId().toString();
        
        log.info("Publishing event to Kafka - Topic: {}, Type: {}, Key: {}", 
            topic, event.getEventType(), key);
        
        return kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event published successfully - Type: {}, Offset: {}", 
                        event.getEventType(), 
                        result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event - Type: {}", event.getEventType(), ex);
                }
            });
    }

    /**
     * Fallback method when Kafka is unavailable
     */
    private CompletableFuture<SendResult<String, Object>> publishFallback(
            BaseEvent event, Exception ex) {
        log.error("Kafka circuit breaker activated. Event will be retried later: {}", 
            event.getEventType(), ex);
        return CompletableFuture.failedFuture(ex);
    }

    private String determineTopicByEventType(String eventType) {
        return switch (eventType) {
            case "UserCreatedEvent" -> "user-created";
            case "UserVerifiedEvent" -> "user-verified";
            case "UserUpdatedEvent" -> "user-updated";
            case "UserDeletedEvent" -> "user-deleted";
            case "UserRoleAssignedEvent" -> "user-role-assigned";
            case "UserLoginEvent" -> "user-login";
            default -> userEventsTopic;
        };
    }
}
