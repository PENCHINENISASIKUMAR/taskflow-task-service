package com.taskflow.taskservice.repository;

import com.taskflow.taskservice.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {
    @Autowired TaskRepository repository;

    @Test void searchesByTitleStatusAndUser() {
        Task task = new Task(); task.setUserId(9L); task.setTitle("Prepare Report");
        task.setStatus(Task.Status.PENDING); task.setPriority(Task.Priority.HIGH);
        repository.saveAndFlush(task);
        assertEquals(1, repository.findByUserIdAndTitleContainingIgnoreCase(9L,"report").size());
        assertEquals(1, repository.findByUserIdAndTitleContainingIgnoreCaseAndStatus(9L,"REPORT", Task.Status.PENDING).size());
        assertEquals(1, repository.findByUserId(9L).size());
        assertTrue(repository.findByUserIdAndStatus(9L,Task.Status.COMPLETED).isEmpty());
    }
}
