package com.taskflow.taskservice.service;

import com.taskflow.taskservice.dto.*;
import com.taskflow.taskservice.entity.Task;
import com.taskflow.taskservice.exception.ResourceNotFoundException;
import com.taskflow.taskservice.repository.TaskRepository;
import java.util.List;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
public class TaskServiceImpl implements TaskService {
    private static final Logger log=LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository repo;
    public TaskServiceImpl(TaskRepository repo){this.repo=repo;}
    private TaskResponse view(Task t){return new TaskResponse(t.getId(),t.getUserId(),t.getTitle(),t.getDescription(),t.getStatus(),t.getPriority(),t.getDueDate(),t.getCreatedAt(),t.getUpdatedAt());}
    private Task required(Long id,Long owner){return repo.findByIdAndUserId(id,owner).orElseThrow(()->new ResourceNotFoundException("Task not found"));}
    public TaskResponse create(TaskRequest r,Long owner){Task t=new Task();t.setUserId(owner);t.setTitle(r.title());t.setDescription(r.description());t.setStatus(r.status()==null?Task.Status.PENDING:r.status());t.setPriority(r.priority()==null?Task.Priority.MEDIUM:r.priority());t.setDueDate(r.dueDate());log.info("Creating task for authenticated user {}",owner);return view(repo.save(t));}
    @Transactional(readOnly=true) public List<TaskResponse> getAll(Long owner){return repo.findByUserId(owner).stream().map(this::view).toList();}
    @Transactional(readOnly=true) public TaskResponse getById(Long id,Long owner){return view(required(id,owner));}
    public TaskResponse update(Long id,Long owner,TaskUpdateRequest r){Task t=required(id,owner);t.setTitle(r.title());t.setDescription(r.description());t.setStatus(r.status());t.setPriority(r.priority());t.setDueDate(r.dueDate());log.info("Updated task {} for authenticated user {}",id,owner);return view(repo.save(t));}
    public void delete(Long id,Long owner){Task t=required(id,owner);repo.delete(t);log.info("Deleted task {} for authenticated user {}",id,owner);}
    @Transactional(readOnly=true) public List<TaskResponse> search(String title,Long owner){return repo.findByUserIdAndTitleContainingIgnoreCase(owner,title).stream().map(this::view).toList();}
    @Transactional(readOnly=true) public List<TaskResponse> filter(String title,Task.Status status,Long owner){if(status!=null&&title!=null)return repo.findByUserIdAndTitleContainingIgnoreCaseAndStatus(owner,title,status).stream().map(this::view).toList();if(status!=null)return repo.findByUserIdAndStatus(owner,status).stream().map(this::view).toList();if(title!=null)return search(title,owner);return getAll(owner);}
}
