package com.s2.todo.backend.service;

import com.s2.todo.backend.model.Todo;
import com.s2.todo.backend.repository.TodoRepository;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Todo items.
 */
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /**
     * Save Todo item.
     *
     * @param todo The Todo item to be saved.
     * @return The saved Todo.
     */
    public Todo save(Todo todo) {
        return todoRepository.save(todo);
    }

    /**
     * Mark a Todo item as "done".
     *
     * @param id The ID of the Todo item to be marked as "done".
     * @return The Todo marked as "done" if found, or null if the Todo item does not exist.
     */
    public Todo markAsDone(Long id) {
        return updateStatus(id, Todo.Status.DONE);
    }

    /**
     * Mark a Todo item as "not done".
     *
     * @param id The ID of the Todo item to be marked as "not done".
     * @return The Todo marked as "not done" if found, or null if the Todo item does not exist.
     */
    public Todo markAsNotDone(Long id) {
        return updateStatus(id, Todo.Status.NOT_DONE);
    }

    private Todo updateStatus(Long id, Todo.Status status) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isPresent()) {
            Todo todo = optionalTodo.get();
            todo.setStatus(status);
            return todoRepository.save(todo);
        }
        return null; // Handle not found case
    }

    /**
     * Get a list of all Todo items.
     *
     * @return The list of all Todo items.
     */
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    /**
     * Get a list of all "not done" Todo items.
     *
     * @return The list of "not done" Todo items.
     */
    public List<Todo> getNotDoneTodos() {
        return todoRepository.findByStatus(Todo.Status.NOT_DONE);
    }

    /**
     * Get details of a specific Todo item.
     *
     * @param id The ID of the Todo item to retrieve details for.
     * @return The Todo details if found, or null if the Todo item does not exist.
     */
    public Todo getTodoDetails(Long id) {
        return todoRepository.findById(id).orElse(null);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Daily at midnight
    public void updateStatusForPastDueItems() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Todo> pastDueItems = todoRepository.findByStatusAndDueDateTimeLessThan(Todo.Status.NOT_DONE, currentDateTime);
        for (Todo todo : pastDueItems) {
            if (todo.getStatus() != Todo.Status.PAST_DUE) {
                todo.setStatus(Todo.Status.PAST_DUE);
                todoRepository.save(todo);
            }
        }
    }

}
