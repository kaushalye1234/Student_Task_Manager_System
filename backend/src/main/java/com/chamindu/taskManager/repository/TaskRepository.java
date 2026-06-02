package com.chamindu.taskManager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.chamindu.taskManager.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    
    Optional<Task> findByIdAndUserId(Long id, Long userId);
    
}


//allow to perform CRUD operations on Task entities without needing to write any SQL queries.