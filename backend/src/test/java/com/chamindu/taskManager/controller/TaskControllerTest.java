package com.chamindu.taskManager.controller;

import com.chamindu.taskManager.exception.ResourceNotFoundException;
import com.chamindu.taskManager.exception.UnauthorizedException;
import com.chamindu.taskManager.model.AppUser;
import com.chamindu.taskManager.model.Task;
import com.chamindu.taskManager.repository.AppUserRepository;
import com.chamindu.taskManager.repository.TaskRepository;
import com.chamindu.taskManager.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TaskController taskController;

    private AppUser user;

    private final String token = "valid-test-token";

    private final String authorizationHeader =
            "Bearer " + token;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setFullName("Test Student");
        user.setEmail("student@example.com");
    }

    private void prepareAuthenticatedUser() {
        when(jwtUtil.isTokenValid(token))
                .thenReturn(true);

        when(jwtUtil.getEmailFromToken(token))
                .thenReturn(user.getEmail());

        when(appUserRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
    }

    @Test
    void getAllTasksReturnsCurrentUsersTasks() {
        prepareAuthenticatedUser();

        Task task = new Task();
        task.setId(10L);
        task.setTitle("Study automated testing");

        when(taskRepository.findByUserId(user.getId()))
                .thenReturn(List.of(task));

        List<Task> result =
                taskController.getAllTasks(authorizationHeader);

        assertEquals(1, result.size());
        assertEquals(
                Long.valueOf(10L),
                result.get(0).getId()
        );

        verify(taskRepository)
                .findByUserId(user.getId());
    }

    @Test
    void createTaskAssignsCurrentUserAndSavesTask() {
        prepareAuthenticatedUser();

        Task newTask = new Task();
        newTask.setTitle("Write backend tests");

        when(taskRepository.save(newTask))
                .thenAnswer(invocation -> {
                    Task savedTask = invocation.getArgument(0);
                    savedTask.setId(20L);
                    return savedTask;
                });

        Task result = taskController.createTask(
                authorizationHeader,
                newTask
        );

        assertEquals(
                Long.valueOf(20L),
                result.getId()
        );

        assertSame(
                user,
                result.getUser()
        );

        verify(taskRepository).save(newTask);
    }

    @Test
    void missingAuthorizationHeaderThrowsUnauthorizedException() {
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> taskController.getAllTasks(null)
        );

        assertEquals(
                "Missing or invalid Authorization header",
                exception.getMessage()
        );

        verifyNoInteractions(taskRepository);
    }

    @Test
    void missingTaskThrowsResourceNotFoundException() {
        prepareAuthenticatedUser();

        when(taskRepository.findByIdAndUserId(
                999L,
                user.getId()
        )).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskController.getTaskById(
                        999L,
                        authorizationHeader
                )
        );

        assertTrue(
                exception.getMessage().contains("Task not found")
        );
    }
}