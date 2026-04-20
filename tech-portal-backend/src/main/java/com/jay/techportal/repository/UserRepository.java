package com.jay.techportal.repository;

import com.jay.techportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 自动生成 SQL: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);
}