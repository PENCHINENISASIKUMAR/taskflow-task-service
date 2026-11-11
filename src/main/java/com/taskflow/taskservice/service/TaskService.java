package com.taskflow.taskservice.service;
import com.taskflow.taskservice.dto.*; import com.taskflow.taskservice.entity.Task; import java.util.List;
public interface TaskService {
    TaskResponse create(TaskRequest request,Long ownerId); List<TaskResponse> getAll(Long ownerId);
    TaskResponse getById(Long id,Long ownerId); TaskResponse update(Long id,Long ownerId,TaskUpdateRequest request);
    void delete(Long id,Long ownerId); List<TaskResponse> search(String title,Long ownerId);
    List<TaskResponse> filter(String title,Task.Status status,Long ownerId);
}
