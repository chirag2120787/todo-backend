package com.s2.todo.backend.controller;

import com.s2.todo.backend.model.Todo;
import com.s2.todo.backend.service.TodoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * A REST-ful controller for managing Todo items.
 */
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

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
        Todo createdTodo = todoService.save(todo);
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

        Todo todo = todoService.getTodoDetails(id);

        if (todo == null) {
            return ResponseEntity.notFound().build();
        }

        if (todo.getStatus() == Todo.Status.PAST_DUE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot mark past due item as not done.");
        }

        todo.setDescription(newDescription);

        Todo updatedTodo = todoService.save(todo);
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
        Todo markedAsDoneTodo = todoService.markAsDone(id);
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
        Todo todo = todoService.getTodoDetails(id);

        if (todo == null) {
            return ResponseEntity.notFound().build();
        }

        if (todo.getStatus() == Todo.Status.PAST_DUE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot mark past due item as not done.");
        }

        todo.setStatus(Todo.Status.NOT_DONE);
        todoService.save(todo);

        return ResponseEntity.ok(todo);
    }

    /**
     * Get a list of all Todo items that are "not done" (with an option to retrieve all items).
     *
     * @param allItems If true, retrieve all Todo items; if false (default), retrieve only "not done" items.
     * @return ResponseEntity containing the list of Todo items and HTTP status code 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos(@RequestParam(required = false, defaultValue = "false") boolean allItems) {
        List<Todo> todos = allItems ? todoService.getAllTodos() : todoService.getNotDoneTodos();
        return ResponseEntity.ok(todos);
    }

    /**
     * Get details of a specific Todo item.
     *
     * @param id The ID of the Todo item to retrieve details for.
     * @return ResponseEntity containing the Todo details and HTTP status code 200 (OK) if found,
     *         or HTTP status code 404 (Not Found) if the Todo item does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoDetails(@PathVariable Long id) {
        Todo todo = todoService.getTodoDetails(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(todo);
    }
}
