package com.user_service.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.user_service.enums.OutboxStatus;
import com.user_service.models.Outbox;

import feign.Param;

public interface OutboxRepository extends JpaRepository<Outbox, UUID>{

	List<Outbox> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);
	
	@Query("SELECT o from Outbox o WHERE o.status = :status AND o.retryCount < 3 ORDER BY o.createdAt ASC")
	List<Outbox> findPendingEvents(@Param("status")OutboxStatus status, Pageable pageable);
	
	@Modifying
    @Query("DELETE FROM Outbox o WHERE o.status = 'PUBLISHED' AND o.publishedAt < :before")
    int deleteOldPublishedEvents(@Param("before") LocalDateTime before);
    
    long countByStatus(OutboxStatus status);
    
    @Modifying
    @Query(" UPDATE Outbox o SET o.status = 'PUBLISHED', o.publishedAt = CURRENT_TIMESTAMP WHERE o.id = :id ")
    int markAsPublished(@Param("id") UUID id);
    
    @Modifying
    @Query("UPDATE Outbox o SET o.status = 'PROCESSING' WHERE o.id = :id")
    int markAsProcessing(@Param("id") UUID id);
}
