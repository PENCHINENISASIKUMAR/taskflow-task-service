package com.taskflow.taskservice.controller;

import com.taskflow.taskservice.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;
import com.taskflow.taskservice.config.SecurityConfig;
import com.taskflow.taskservice.config.SecurityErrorHandlers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.taskflow.taskservice.dto.TaskResponse;
import com.taskflow.taskservice.entity.Task;
import java.time.LocalDateTime;

@WebMvcTest(TaskController.class)
@TestPropertySource(properties="taskflow.jwt.secret-base64=VGhpcy1pcy1hLXRlc3Qta2V5LWZvci1qd3QtMzItYnl0ZXM=")
@Import({SecurityConfig.class, SecurityErrorHandlers.class})
class TaskControllerTest {
    @Autowired MockMvc mvc;
    @MockBean TaskService service;

    @Test void rejectsInvalidTaskAndReturnsStructuredValidationError() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\" \"}").with(jwt().jwt(j -> j.subject("9"))).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/tasks"));
    }

    @Test void derivesTaskOwnerFromJwtInsteadOfRequestBody() throws Exception {
        when(service.create(any(),eq(9L))).thenReturn(new TaskResponse(1L,9L,"Owned",null,Task.Status.PENDING,Task.Priority.MEDIUM,null,LocalDateTime.now(),LocalDateTime.now()));
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Owned\",\"userId\":999}").with(jwt().jwt(j -> j.subject("9"))).with(csrf()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.userId").value(9));
        verify(service).create(any(),eq(9L));
    }
}
