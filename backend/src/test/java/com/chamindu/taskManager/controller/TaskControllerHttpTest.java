package com.chamindu.taskManager.controller;

import com.chamindu.taskManager.exception.GlobalExceptionHandler;
import com.chamindu.taskManager.model.AppUser;
import com.chamindu.taskManager.repository.AppUserRepository;
import com.chamindu.taskManager.repository.TaskRepository;
import com.chamindu.taskManager.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerHttpTest {

    private MockMvc mockMvc;

    private TaskRepository taskRepository;
    private AppUserRepository appUserRepository;
    private JwtUtil jwtUtil;

    private static final String TOKEN =
            "valid-test-token";

    private static final String AUTH_HEADER =
            "Bearer " + TOKEN;

    @BeforeEach
    void setUp() {

        taskRepository =
                mock(TaskRepository.class);

        appUserRepository =
                mock(AppUserRepository.class);

        jwtUtil =
                mock(JwtUtil.class);

        TaskController taskController =
                new TaskController(
                        taskRepository,
                        appUserRepository,
                        jwtUtil
                );

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(taskController)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .setValidator(validator)
                .build();
    }

    private AppUser prepareAuthenticatedUser() {

        AppUser user = new AppUser();

        user.setId(1L);
        user.setFullName("Test Student");
        user.setEmail("student@example.com");

        when(jwtUtil.isTokenValid(TOKEN))
                .thenReturn(true);

        when(jwtUtil.getEmailFromToken(TOKEN))
                .thenReturn(user.getEmail());

        when(appUserRepository.findByEmail(
                user.getEmail()
        )).thenReturn(Optional.of(user));

        return user;
    }

    @Test
    void createTaskReturns400ForValidationErrors()
            throws Exception {

        String invalidTaskJson = """
                {
                  "title": "",
                  "description": "Validation test",
                  "status": "PENDING",
                  "dueDate": "2020-01-01",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(
                        post("/api/tasks")
                                .header(
                                        "Authorization",
                                        AUTH_HEADER
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(invalidTaskJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Validation Failed")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        containsString("title")
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        containsString("dueDate")
                                )
                );
    }

    @Test
    void createTaskReturns400ForInvalidEnumValue()
            throws Exception {

        String invalidTaskJson = """
                {
                  "title": "Test task",
                  "description": "Invalid enum test",
                  "status": "UNKNOWN_STATUS",
                  "dueDate": "2099-01-01",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(
                        post("/api/tasks")
                                .header(
                                        "Authorization",
                                        AUTH_HEADER
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(invalidTaskJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value(
                                        "Invalid Request Body"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "The request contains invalid JSON or an invalid field value"
                                )
                );
    }

    @Test
    void getTasksReturns401WithoutAuthorizationHeader()
            throws Exception {

        mockMvc.perform(
                        get("/api/tasks")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(
                        jsonPath("$.status")
                                .value(401)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Unauthorized")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Missing or invalid Authorization header"
                                )
                );
    }

    @Test
    void getTaskReturns400ForInvalidTaskId()
            throws Exception {

        mockMvc.perform(
                        get("/api/tasks/not-a-number")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Invalid Parameter")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "id has an invalid value"
                                )
                );
    }

    @Test
    void getTaskReturns404WhenTaskDoesNotExist()
            throws Exception {

        AppUser user =
                prepareAuthenticatedUser();

        when(taskRepository.findByIdAndUserId(
                999L,
                user.getId()
        )).thenReturn(Optional.empty());

        mockMvc.perform(
                        get("/api/tasks/999")
                                .header(
                                        "Authorization",
                                        AUTH_HEADER
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Not Found")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Task not found")
                );
    }
}