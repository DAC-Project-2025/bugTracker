package com.user_service.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.user_service.enums.OutboxStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbox", indexes = {
    @Index(name = "idx_status_created", columnList = "status, created_at"),
    @Index(name = "idx_aggregate_id", columnList = "aggregate_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Events are stored here transactionally with business logic,
//then published to Kafka asynchronously.
public class Outbox {
	
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;  // User ID
    
    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;  // "User"
    
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;  // "UserCreatedEvent", "UserUpdatedEvent", etc.
    
    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;  // JSON serialized event
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private OutboxStatus status = OutboxStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    // Helper methods
    public void markAsPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String error) {
        this.status = OutboxStatus.FAILED;
        this.errorMessage = error;
        this.retryCount++;
    }
    
    public boolean canRetry() {
        return this.retryCount < 3;
    }
}
