package com.chamindu.taskManager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.chamindu.taskManager.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    
}


//allow to perform CRUD operations on Task entities without needing to write any SQL queries.