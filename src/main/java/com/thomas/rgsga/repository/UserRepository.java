package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByResetToken(String resetToken);

    Optional<User> findByRefreshToken(String refreshToken);
}
