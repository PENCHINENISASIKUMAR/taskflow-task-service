package com.taskflow.taskservice.service;

import com.taskflow.taskservice.dto.TaskRequest;
import com.taskflow.taskservice.entity.Task;
import com.taskflow.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;
import com.taskflow.taskservice.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock TaskRepository repository;
    @InjectMocks TaskServiceImpl service;

    @Test void createDerivesOwnerFromAuthenticatedIdentity() {
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.create(new TaskRequest("Write tests", null, null, null, null), 7L);
        assertEquals(7L, response.userId());
        assertEquals(Task.Status.PENDING, response.status());
        assertEquals(Task.Priority.MEDIUM, response.priority());
        verify(repository).save(any(Task.class));
    }

    @Test void cannotReadAnotherUsersTask() {
        when(repository.findByIdAndUserId(5L, 7L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(5L, 7L));
        verify(repository).findByIdAndUserId(5L, 7L);
        verify(repository, never()).findById(5L);
    }
}
