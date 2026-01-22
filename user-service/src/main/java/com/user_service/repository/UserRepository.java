package com.user_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.user_service.enums.AuthProvider;
import com.user_service.enums.UserStatus;
import com.user_service.models.User;

/**
 * Repository interface for {@link User} entity.
 * 
 * <p>
 * This interface extends {@link JpaRepository}, which provides standard CRUD operations
 * and query method support for the {@link User} entity with a primary key of type {@code Long}.
 * </p>
 *
 * <p>
 * Spring Data JPA will automatically provide the implementation at runtime.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, UUID> {
Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByOauthProviderIdAndAuthProvider(String oauthProviderId, AuthProvider authProvider);
    
    Optional<User> findByEmailVerificationToken(String token);
    
    List<User> findByStatus(UserStatus status);
    
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActive();
    
    @Query("SELECT u FROM User u JOIN u.userRoles ur WHERE ur.projectId = :projectId AND u.deletedAt IS NULL")
    List<User> findByProjectId(@Param("projectId") UUID projectId);
    
    @Query("SELECT u FROM User u JOIN u.userRoles ur WHERE ur.role.name = :roleName AND ur.projectId = :projectId")
    List<User> findByRoleAndProject(@Param("roleName") String roleName, @Param("projectId") UUID projectId);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status AND u.createdAt >= :since")
    long countByStatusAndCreatedAtAfter(@Param("status") UserStatus status, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLogin >= :since")
    long countActiveUsersSince(@Param("since") LocalDateTime since);
    
    long countByStatus(UserStatus status);
    
    
}
