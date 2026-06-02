package com.chamindu.taskManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.chamindu.taskManager.model.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
     Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);
    
}
