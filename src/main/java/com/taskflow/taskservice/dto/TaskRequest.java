package com.taskflow.taskservice.dto;
import com.taskflow.taskservice.entity.Task.Priority; import com.taskflow.taskservice.entity.Task.Status; import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.Size; import java.time.LocalDate;
public record TaskRequest(@NotBlank @Size(max=160) String title,@Size(max=2000) String description,Status status,Priority priority,LocalDate dueDate) {}
