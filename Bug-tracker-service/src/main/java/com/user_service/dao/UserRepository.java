package com.user_service.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.dto.ProfileDTO;
import com.user_service.enums.Role;
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
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Custom finder method to retrieve a user by email.
     * Spring Data JPA automatically generates the implementation based on method name.
     *
     * @param email the email address of the user
     * @return the user entity associated with the given email, or {@code null} if not found
     */
    public Optional<User> findByEmail(String email);
    public List<User> findByRole(Role role);
    
}
