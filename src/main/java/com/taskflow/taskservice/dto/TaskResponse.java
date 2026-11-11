package com.taskflow.taskservice.dto;
import com.taskflow.taskservice.entity.Task.*; import java.time.*;
public record TaskResponse(Long id,Long userId,String title,String description,Status status,Priority priority,LocalDate dueDate,LocalDateTime createdAt,LocalDateTime updatedAt) {}
