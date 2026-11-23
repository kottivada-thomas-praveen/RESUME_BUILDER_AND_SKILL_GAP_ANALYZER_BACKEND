package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.Role;
import com.thomas.rgsga.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByResetToken(String resetToken);

    Optional<User> findByRefreshToken(String refreshToken);

    boolean existsByRole(Role role);

    Long countByRole(Role role);

    Long countByAccountEnabledTrue();

    Long countByAccountEnabledFalse();

    Long countByCreatedAtAfter(LocalDateTime since);

    Long countByLastLoginAtAfter(LocalDateTime since);

    @Query("SELECT u FROM User u WHERE " +
           "(:query IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:enabled IS NULL OR u.accountEnabled = :enabled)")
    Page<User> searchUsers(@Param("query") String query,
                           @Param("role") Role role,
                           @Param("enabled") Boolean enabled,
                           Pageable pageable);

    List<User> findTop10ByOrderByCreatedAtDesc();

    List<User> findAllByOrderByCreatedAtDesc();
}
