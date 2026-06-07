package com.chamindu.taskManager.controller;

import com.chamindu.taskManager.model.AppUser;
import com.chamindu.taskManager.model.Task;
import com.chamindu.taskManager.repository.AppUserRepository;
import com.chamindu.taskManager.repository.TaskRepository;
import com.chamindu.taskManager.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class TaskController {

    private final TaskRepository taskRepository;
    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;

    public TaskController(
            TaskRepository taskRepository,
            AppUserRepository appUserRepository,
            JwtUtil jwtUtil
    ) {
        this.taskRepository = taskRepository;
        this.appUserRepository = appUserRepository;
        this.jwtUtil = jwtUtil;
    }

    private AppUser getCurrentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(token);

        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public List<Task> getAllTasks(@RequestHeader("Authorization") String authHeader) {
        AppUser user = getCurrentUser(authHeader);
        return taskRepository.findByUserId(user.getId());
    }

    @PostMapping
    public Task createTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Task task
    ) {
        AppUser user = getCurrentUser(authHeader);
        task.setUser(user);
        return taskRepository.save(task);
    }

    @GetMapping("/{id}")
    public Task getTaskById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        AppUser user = getCurrentUser(authHeader);

        return taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Task not found for this user"));
    }

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Task updatedTask
    ) {
        AppUser user = getCurrentUser(authHeader);

        Task existingTask = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Task not found for this user"));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setDueDate(updatedTask.getDueDate());

        return taskRepository.save(existingTask);
    }

    @DeleteMapping("/{id}")
    public String deleteTask(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        AppUser user = getCurrentUser(authHeader);

        Task existingTask = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Task not found for this user"));

        taskRepository.delete(existingTask);

        return "Task deleted successfully";
    }
}