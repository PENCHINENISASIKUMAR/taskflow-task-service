package com.taskflow.taskservice.dto;
import com.taskflow.taskservice.entity.Task.*; import jakarta.validation.constraints.*; import java.time.LocalDate;
public record TaskUpdateRequest(@NotBlank @Size(max=160) String title,@Size(max=2000) String description,@NotNull Status status,@NotNull Priority priority,LocalDate dueDate) {}
