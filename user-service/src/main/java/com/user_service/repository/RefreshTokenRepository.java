package com.user_service.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.user_service.models.RefreshToken;

import feign.Param;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

	void deleteByUserId(UUID userId);

	@Modifying
	@Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now WHERE rt.user.id = :userId")
	void revokeAllByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
	int deleteExpiredTokens(@Param("now") LocalDateTime now);

	long countByUserIdAndRevokedFalse(UUID userId);
}
