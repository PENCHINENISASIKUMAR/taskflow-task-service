package com.taskflow.taskservice.repository;
import com.taskflow.taskservice.entity.Task; import java.util.List; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByUserIdAndStatus(Long userId,Task.Status status);
    List<Task> findByUserIdAndTitleContainingIgnoreCase(Long userId,String title);
    List<Task> findByUserIdAndTitleContainingIgnoreCaseAndStatus(Long userId,String title,Task.Status status);
    Optional<Task> findByIdAndUserId(Long id,Long userId);
}
