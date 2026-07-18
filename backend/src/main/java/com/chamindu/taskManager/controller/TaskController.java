package com.chamindu.taskManager.controller;

import com.chamindu.taskManager.model.AppUser;
import com.chamindu.taskManager.model.Task;
import com.chamindu.taskManager.repository.AppUserRepository;
import com.chamindu.taskManager.repository.TaskRepository;
import com.chamindu.taskManager.security.JwtUtil;
import com.chamindu.taskManager.exception.UnauthorizedException;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chamindu.taskManager.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Tasks", description = "Create, view, update and delete tasks belonging to the logged-in user")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskRepository taskRepository;
    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;

    public TaskController(
            TaskRepository taskRepository,
            AppUserRepository appUserRepository,
            JwtUtil jwtUtil) {
        this.taskRepository = taskRepository;
        this.appUserRepository = appUserRepository;
        this.jwtUtil = jwtUtil;
    }

    private AppUser getCurrentUser(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Task API request rejected: missing or invalid Authorization header");

            throw new UnauthorizedException(
                    "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            log.warn("Task API request rejected: invalid or expired JWT");

            throw new UnauthorizedException(
                    "Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(token);

        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("JWT belongs to a user that no longer exists");

                    return new UnauthorizedException("User not found");
                });
    }

    @GetMapping
    public List<Task> getAllTasks(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        AppUser user = getCurrentUser(authHeader);

        List<Task> tasks = taskRepository.findByUserId(user.getId());

        log.info(
                "Retrieved {} tasks for userId={}",
                tasks.size(),
                user.getId());

        return tasks;
    }

    @PostMapping
    public Task createTask(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody Task task) {
        AppUser user = getCurrentUser(authHeader);

        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        log.info(
                "Created task id={} for userId={}",
                savedTask.getId(),
                user.getId());

        return savedTask;
    }

    @GetMapping("/{id}")
    public Task getTaskById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        AppUser user = getCurrentUser(authHeader);

        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        log.info(
                "Retrieved task id={} for userId={}",
                task.getId(),
                user.getId());

        return task;
    }

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody Task updatedTask) {
        AppUser user = getCurrentUser(authHeader);

        Task existingTask = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found for this user"));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setPriority(updatedTask.getPriority());

        Task savedTask = taskRepository.save(existingTask);

        log.info(
                "Updated task id={} for userId={}",
                savedTask.getId(),
                user.getId());

        return savedTask;
    }

    @DeleteMapping("/{id}")
    public String deleteTask(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        AppUser user = getCurrentUser(authHeader);

        Task existingTask = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        taskRepository.delete(existingTask);
        
        log.info(
                "Deleted task id={} for userId={}",
                existingTask.getId(),
                user.getId());


        return "Task deleted successfully";
    }
}