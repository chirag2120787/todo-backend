package com.s2.todo.backend.repository;

import com.s2.todo.backend.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing Todo items.
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * Find Todo items by their status.
     *
     * @param status The status of the Todo items to retrieve.
     * @return A list of Todo items with the specified status.
     */
    List<Todo> findByStatus(Todo.Status status);

    /**
     * Find Todo items by their status and due date/time.
     *
     * @param status      The status of the Todo items to retrieve.
     * @param dueDateTime The due date/time for the Todo items to retrieve.
     * @return A list of Todo items with the specified status and due date/time.
     */
    List<Todo> findByStatusAndDueDateTimeLessThan(Todo.Status status, LocalDateTime dueDateTime);

    /**
     * Find Todo items by their completion status.
     *
     * @param done     Indicates whether the Todo items are completed or not.
     * @param pageable Pageable object for pagination.
     * @return A page of Todo items based on their completion status.
     */
    Page<Todo> findByDone(boolean done, Pageable pageable);
}

