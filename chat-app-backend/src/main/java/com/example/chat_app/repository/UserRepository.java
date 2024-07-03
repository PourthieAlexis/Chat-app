package com.example.chat_app.repository;

import com.example.chat_app.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    boolean existsById(@NonNull Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
