package com.s2.todo.backend.service;

import com.s2.todo.backend.model.Todo;
import com.s2.todo.backend.repository.TodoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service class for managing Todo items.
 */
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final Logger logger = LoggerFactory.getLogger(TodoService.class);

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /**
     * Save a Todo item.
     *
     * @param todo The Todo item to be saved.
     * @return The saved Todo.
     */
    public Todo save(Todo todo) {
        logger.info("Saving a Todo item: {}", todo);
        Todo savedTodo = todoRepository.save(todo);
        logger.info("Saved Todo item with ID: {}", savedTodo.getId());
        return savedTodo;
    }

    /**
     * Mark a Todo item as "done".
     *
     * @param id The ID of the Todo item to be marked as "done".
     * @return The Todo marked as "done" if found, or null if the Todo item does not exist.
     */
    public Todo markAsDone(Long id) {
        logger.info("Marking Todo item with ID {} as 'done'", id);
        return updateStatus(id, Todo.Status.DONE);
    }

    /**
     * Update status of a Todo item.
     *
     * @param id The ID of the Todo item to be marked as "not done".
     * @param status The Status of the Todo item to update to.
     * @return The Todo marked as new status if found, or null if the Todo item does not exist.
     */
    private Todo updateStatus(Long id, Todo.Status status) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isPresent()) {
            Todo todo = optionalTodo.get();
            todo.setStatus(status);
            logger.info("Updating status for Todo item with ID {}: {}", id, status);
            Todo updatedTodo = todoRepository.save(todo);
            logger.info("Updated status for Todo item with ID {}: {}", id, updatedTodo.getStatus());
            return updatedTodo;
        }
        logger.warn("Todo item with ID {} not found. Unable to update status.", id);
        return null; // Handle not found case
    }

    /**
     * Get a list of all Todo items.
     *
     * @param page     The page number (1-based).
     * @param pageSize The number of items per page.
     * @return The list of all Todo items.
     */
    public Page<Todo> getAllTodos(int page, int pageSize) {
        logger.info("Retrieving all Todo items (page={}, pageSize={})", page, pageSize);
        Pageable pageable = PageRequest.of(page - 1, pageSize); // Page numbers are 1-based, so we subtract 1
        Page<Todo> todosPage = todoRepository.findAll(pageable);
        logger.info("Retrieved {} Todo items.", todosPage.getNumberOfElements());
        return todosPage;
    }

    /**
     * Get a list of all "not done" Todo items.
     *
     * @param page     The page number (1-based).
     * @param pageSize The number of items per page.
     * @return The list of "not done" Todo items.
     */
    public Page<Todo> getNotDoneTodos(int page, int pageSize) {
        logger.info("Retrieving 'not done' Todo items (page={}, pageSize={})", page, pageSize);
        Pageable pageable = PageRequest.of(page - 1, pageSize); // Page numbers are 1-based, so we subtract 1
        Page<Todo> todosPage = todoRepository.findByDone(false, pageable);
        logger.info("Retrieved {} 'not done' Todo items.", todosPage.getNumberOfElements());
        return todosPage;
    }

    /**
     * Get details of a specific Todo item.
     *
     * @param id The ID of the Todo item to retrieve details for.
     * @return The Todo details if found, or null if the Todo item does not exist.
     */
    public Todo getTodoDetails(Long id) {
        logger.info("Retrieving details for Todo item with ID: {}", id);
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo == null) {
            logger.warn("Todo item with ID {} not found.", id);
        } else {
            logger.info("Retrieved details for Todo item with ID {}: {}", id, todo);
        }
        return todo;
    }

    /**
     * Scheduled task to update the status of past due Todo items daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Daily at midnight
    public void updateStatusForPastDueItems() {
        logger.info("Running scheduled task to update status for past due items.");
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Todo> pastDueItems =
            todoRepository.findByStatusAndDueDateTimeLessThan(Todo.Status.NOT_DONE, currentDateTime);
        for (Todo todo : pastDueItems) {
            if (todo.getStatus() != Todo.Status.PAST_DUE) {
                todo.setStatus(Todo.Status.PAST_DUE);
                logger.info("Updated status for past due Todo item with ID {}: {}", todo.getId(), Todo.Status.PAST_DUE);
                todoRepository.save(todo);
            }
        }
        logger.info("Scheduled task completed.");
    }

}
