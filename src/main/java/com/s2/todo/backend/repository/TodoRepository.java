package com.s2.todo.backend.repository;

import com.s2.todo.backend.model.Todo;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByStatus(Todo.Status status);

    List<Todo> findByStatusAndDueDateTimeLessThan(Todo.Status status, LocalDateTime dueDateTime);
}
