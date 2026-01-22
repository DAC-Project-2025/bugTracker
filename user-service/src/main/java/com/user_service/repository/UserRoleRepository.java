package com.user_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.user_service.models.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    List<UserRole> findByUserId(UUID userId);
    
    List<UserRole> findByUserIdAndProjectId(UUID userId, UUID projectId);
    
    List<UserRole> findByProjectId(UUID projectId);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId AND ur.projectId IS NULL")
    List<UserRole> findSystemWideRolesByUserId(@Param("userId") UUID userId);
    
    boolean existsByUserIdAndRoleIdAndProjectId(UUID userId, Long roleId, UUID projectId);
    
    void deleteByUserIdAndProjectId(UUID userId, UUID projectId);
    
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.projectId = :projectId")
    long countByProjectId(@Param("projectId") UUID projectId);
}