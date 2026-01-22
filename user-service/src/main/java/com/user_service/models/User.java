package com.user_service.models;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.user_service.enums.AuthProvider;
import com.user_service.enums.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "users",
indexes = {
		@Index(name = "", columnList = "email"),
		@Index(name = "idx_status", columnList = "status"),
	    @Index(name = "idx_auth_provider", columnList = "auth_provider")
})
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@Column(name = "email", unique = true, nullable = false, length = 255)
	private String email;
	
	@Column(name = "full_name", nullable = false, length = 255)
	private String fullName;
	
	@Column(name = "password_hash", length = 255 )
	private String passwordHash; //not Nullable because of oauth user 
	
	@Column(name = "avatar_url", length = 500)
	private String avatarUrl;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "auth_provider", nullable = false, length = 20)
	private AuthProvider authProvider;
	
	@Column(name = "oauth_provider_id", length = 255)
	private String oauthProviderId; // for google id, etc
	
	@Enumerated(EnumType.STRING)
	@Column(name="status", nullable = false, length = 30)
	@Builder.Default
	private UserStatus status = UserStatus.PENDING_VERIFICATION;
	
	@Column(name = "email_verified")
	@Builder.Default
	private boolean emailVerified = false;  
	
	@Column(name="email_verification_token", length = 500)
	private String emailVerificationToken;

	@Column(name="email_verification_token_expiry")
	private LocalDateTime emailVerificationTokenExpiry;
	
	@Column(name = "last_login")
	private LocalDateTime lastLogin;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false, updatable = false)
	private LocalDateTime updatedAt;
	
	
	@Column(name = "deleted_at", nullable = false, updatable = false)
	private LocalDateTime deletedAt;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();
	
	public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status) && this.deletedAt == null;
    }
    
    public boolean isOAuthUser() {
    	//to check if this user created via OAuth (Google, GitHub, etc) or local signup?
        return !AuthProvider.LOCAL.equals(this.authProvider);
    }
    
    public void markEmailAsVerified() {
        this.emailVerified = true;
        this.status = UserStatus.ACTIVE;
        this.emailVerificationToken = null;
        this.emailVerificationTokenExpiry = null;
    }
    
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    public void softDelete() {
        this.status = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
    
    public boolean getEmailVerified() {
        return emailVerified;
    }
}
