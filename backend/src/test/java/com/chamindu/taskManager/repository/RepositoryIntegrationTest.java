package com.chamindu.taskManager.repository;

import com.chamindu.taskManager.model.AppUser;
import com.chamindu.taskManager.model.Task;
import com.chamindu.taskManager.model.TaskPriority;
import com.chamindu.taskManager.model.TaskStatus;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RepositoryIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser createUser(
            String fullName,
            String email
    ) {
        AppUser user = new AppUser();

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword("encoded-test-password");

        return appUserRepository.saveAndFlush(user);
    }

    private Task createTask(
            AppUser user,
            String title
    ) {
        Task task = new Task();

        task.setTitle(title);
        task.setDescription("Repository integration test");
        task.setStatus(TaskStatus.PENDING);
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setPriority(TaskPriority.MEDIUM);
        task.setUser(user);

        return taskRepository.saveAndFlush(task);
    }

    @Test
    void findByUserIdReturnsOnlyThatUsersTasks() {

        AppUser firstUser = createUser(
                "First Student",
                "first@example.com"
        );

        AppUser secondUser = createUser(
                "Second Student",
                "second@example.com"
        );

        createTask(firstUser, "First user task one");
        createTask(firstUser, "First user task two");
        createTask(secondUser, "Second user task");

        List<Task> result =
                taskRepository.findByUserId(
                        firstUser.getId()
                );

        assertEquals(2, result.size());

        assertTrue(
                result.stream().allMatch(task ->
                        task.getUser()
                                .getId()
                                .equals(firstUser.getId())
                )
        );
    }

    @Test
    void findByIdAndUserIdReturnsTaskForOwner() {

        AppUser owner = createUser(
                "Task Owner",
                "owner@example.com"
        );

        Task savedTask = createTask(
                owner,
                "Owner task"
        );

        Optional<Task> result =
                taskRepository.findByIdAndUserId(
                        savedTask.getId(),
                        owner.getId()
                );

        assertTrue(result.isPresent());

        assertEquals(
                "Owner task",
                result.get().getTitle()
        );
    }

    @Test
    void findByIdAndUserIdReturnsEmptyForDifferentUser() {

        AppUser owner = createUser(
                "Task Owner",
                "owner2@example.com"
        );

        AppUser differentUser = createUser(
                "Different User",
                "different@example.com"
        );

        Task savedTask = createTask(
                owner,
                "Private task"
        );

        Optional<Task> result =
                taskRepository.findByIdAndUserId(
                        savedTask.getId(),
                        differentUser.getId()
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByEmailReturnsSavedUser() {

        AppUser savedUser = createUser(
                "Email Test User",
                "emailtest@example.com"
        );

        Optional<AppUser> result =
                appUserRepository.findByEmail(
                        "emailtest@example.com"
                );

        assertTrue(result.isPresent());

        assertEquals(
                savedUser.getId(),
                result.get().getId()
        );

        assertEquals(
                "Email Test User",
                result.get().getFullName()
        );
    }

    @Test
    void existsByEmailReturnsCorrectResult() {

        createUser(
                "Existing User",
                "existing@example.com"
        );

        boolean existingEmail =
                appUserRepository.existsByEmail(
                        "existing@example.com"
                );

        boolean missingEmail =
                appUserRepository.existsByEmail(
                        "missing@example.com"
                );

        assertTrue(existingEmail);
        assertFalse(missingEmail);
    }
}