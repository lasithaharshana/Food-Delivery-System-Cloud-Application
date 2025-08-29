package com.deliverysystem.authservice.repository;

import com.deliverysystem.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
    
    Boolean existsByUsername(String username);

    Boolean existsByIdAndRole(Long id, User.Role role);

    Optional<User> findByIdAndRole(Long id, User.Role role);

    Boolean existsByEmail(String email);
    
    List<User> findByRole(User.Role role);
    
    List<User> findByIsActive(Boolean isActive);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = :isActive")
    List<User> findByRoleAndIsActive(@Param("role") User.Role role, @Param("isActive") Boolean isActive);
}
