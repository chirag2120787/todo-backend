package com.s2.todo.backend.controller;

import com.s2.todo.backend.model.Todo;
import com.s2.todo.backend.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST-ful controller for managing Todo items.
 */
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;
    private final Logger logger = LoggerFactory.getLogger(TodoController.class);

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * Add a new Todo item.
     *
     * @param todo The Todo item to be added.
     * @return ResponseEntity containing the newly created Todo and HTTP status code 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Todo> addItem(@RequestBody Todo todo) {
        logger.info("Received a request to add a new Todo item.");

        if (todo == null) {
            logger.error("Received an empty request body. Returning a bad request response.");
            return ResponseEntity.badRequest().build();
        }

        Todo createdTodo = todoService.save(todo);
        logger.info("Added a new Todo item with ID: {}", createdTodo.getId());

        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    /**
     * Update the description of a Todo item.
     *
     * @param id             The ID of the Todo item to be updated.
     * @param newDescription The new description for the Todo item.
     * @return ResponseEntity containing the updated Todo and HTTP status code 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDescription(@PathVariable Long id, @RequestBody String newDescription) {
        logger.info("Received a request to update description for Todo item with ID: {}", id);

        // Retrieve the Todo item by ID
        Todo todo = todoService.getTodoDetails(id);

        // Check if the Todo item exists
        if (todo == null) {
            logger.warn("Todo item with ID {} not found. Returning a not found response.", id);
            return ResponseEntity.notFound().build();
        }

        // Check if the Todo item is past due
        if (todo.getStatus() == Todo.Status.PAST_DUE) {
            logger.warn("Cannot update description for a past due Todo item with ID: {}", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot update description for a past due item.");
        }

        // Update the description
        todo.setDescription(newDescription);

        // Save the updated Todo item
        Todo updatedTodo = todoService.save(todo);

        logger.info("Updated description for Todo item with ID: {}", id);

        return ResponseEntity.ok(updatedTodo);
    }

    /**
     * Mark a Todo item as "done".
     *
     * @param id The ID of the Todo item to be marked as "done".
     * @return ResponseEntity containing the Todo marked as "done" and HTTP status code 200 (OK).
     */
    @PutMapping("/{id}/done")
    public ResponseEntity<Todo> markAsDone(@PathVariable Long id) {
        logger.info("Received a request to mark Todo item with ID {} as 'done'", id);

        // Mark the Todo item as 'done'
        Todo markedAsDoneTodo = todoService.markAsDone(id);

        // Check if the Todo item was successfully marked as 'done'
        if (markedAsDoneTodo == null) {
            logger.warn("Todo item with ID {} not found. Returning a not found response.", id);
            return ResponseEntity.notFound().build();
        }

        logger.info("Marked Todo item with ID {} as 'done'", id);

        return ResponseEntity.ok(markedAsDoneTodo);
    }

    /**
     * Mark a Todo item as "not done".
     *
     * @param id The ID of the Todo item to be marked as "not done".
     * @return ResponseEntity containing the Todo marked as "not done" and HTTP status code 200 (OK).
     */
    @PutMapping("/{id}/notdone")
    public ResponseEntity<?> markAsNotDone(@PathVariable Long id) {
        logger.info("Received a request to mark Todo item with ID {} as 'not done'", id);

        // Retrieve the Todo item by ID
        Todo todo = todoService.getTodoDetails(id);

        // Check if the Todo item exists
        if (todo == null) {
            logger.warn("Todo item with ID {} not found. Returning a not found response.", id);
            return ResponseEntity.notFound().build();
        }

        // Check if the Todo item is past due
        if (todo.getStatus() == Todo.Status.PAST_DUE) {
            logger.warn("Cannot mark past due Todo item with ID {} as 'not done'. Returning a forbidden response.", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot mark past due item as not done.");
        }

        // Mark the Todo item as 'not done'
        todo.setStatus(Todo.Status.NOT_DONE);
        todoService.save(todo);

        logger.info("Marked Todo item with ID {} as 'not done'", id);

        return ResponseEntity.ok(todo);
    }

    /**
     * Get a list of all Todo items that are "not done" (with an option to retrieve all items).
     *
     * @param allItems If true, retrieve all Todo items; if false (default), retrieve only "not done" items.
     * @return ResponseEntity containing the list of Todo items and HTTP status code 200 (OK).
     */
    @GetMapping
    public ResponseEntity<Page<Todo>> getAllTodos(
        @RequestParam(required = false, defaultValue = "false") boolean allItems,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        logger.info("Received a request to retrieve Todo items (allItems={}, page={}, pageSize={})", allItems, page,
            pageSize);

        Page<Todo> todosPage;

        if (allItems) {
            todosPage = todoService.getAllTodos(page, pageSize);
        } else {
            todosPage = todoService.getNotDoneTodos(page, pageSize);
        }

        logger.info("Retrieved {} Todo items.", todosPage.getNumberOfElements());

        return ResponseEntity.ok(todosPage);
    }

    /**
     * Get details of a specific Todo item.
     *
     * @param id The ID of the Todo item to retrieve details for.
     * @return ResponseEntity containing the Todo details and HTTP status code 200 (OK) if found,
     * or HTTP status code 404 (Not Found) if the Todo item does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoDetails(@PathVariable Long id) {
        logger.info("Received a request to retrieve details for Todo item with ID: {}", id);

        Todo todo = todoService.getTodoDetails(id);

        if (todo == null) {
            logger.warn("Todo item with ID {} not found. Returning a not found response.", id);
            return ResponseEntity.notFound().build();
        }

        logger.info("Retrieved details for Todo item with ID: {}", id);

        return ResponseEntity.ok(todo);
    }
}
