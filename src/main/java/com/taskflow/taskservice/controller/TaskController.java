package com.taskflow.taskservice.controller;

import com.taskflow.taskservice.dto.*; import com.taskflow.taskservice.entity.Task; import com.taskflow.taskservice.service.TaskService;
import jakarta.validation.Valid; import java.util.List; import org.springframework.http.HttpStatus; import org.springframework.security.oauth2.jwt.Jwt; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;
    public TaskController(TaskService service){this.service=service;}
    private Long owner(Jwt jwt){return Long.valueOf(jwt.getSubject());}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public TaskResponse create(@Valid @RequestBody TaskRequest request,@AuthenticationPrincipal Jwt jwt){return service.create(request,owner(jwt));}
    @GetMapping public List<TaskResponse> all(@RequestParam(required=false) String title,@RequestParam(required=false) Task.Status status,@AuthenticationPrincipal Jwt jwt){return service.filter(title,status,owner(jwt));}
    @GetMapping("/{id}") public TaskResponse get(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){return service.getById(id,owner(jwt));}
    @PutMapping("/{id}") public TaskResponse update(@PathVariable Long id,@Valid @RequestBody TaskUpdateRequest request,@AuthenticationPrincipal Jwt jwt){return service.update(id,owner(jwt),request);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){service.delete(id,owner(jwt));}
    @GetMapping("/search") public List<TaskResponse> search(@RequestParam String title,@AuthenticationPrincipal Jwt jwt){return service.search(title,owner(jwt));}
}
