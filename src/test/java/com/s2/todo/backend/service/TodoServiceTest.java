package com.s2.todo.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.s2.todo.backend.model.Todo;
import com.s2.todo.backend.repository.TodoRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = "com.s2.todo.backend")
public class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @MockBean
    TodoRepository todoRepositoryMock;

    @Test
    public void testGetAllTodos() {
        // Create a list of TODOs
        List<Todo> todoList = Arrays.asList(
            Todo.builder().description("Task 1").status(Todo.Status.NOT_DONE).build(),
            Todo.builder().description("Task 2").status(Todo.Status.DONE).build()
        );

        // Create a Page object from the list (simulating pagination)
        Page<Todo> todoPage = new PageImpl<>(todoList);

        // Mock the repository to return the Page object
        when(todoRepositoryMock.findAll(any(Pageable.class))).thenReturn(todoPage);

        Page<Todo> todos = todoService.getAllTodos(1, 10); // Assuming page 1 and 10 items per page

        // Assertions
        assertThat(todos.getTotalElements()).isEqualTo(2);
        assertThat(todos.getContent().get(0).getDescription()).isEqualTo("Task 1");
        assertThat(todos.getContent().get(1).getStatus()).isEqualTo(Todo.Status.DONE);
    }

    @Test
    public void testAddItem() {
        // Arrange
        Todo todoToAdd = new Todo();
        Todo savedTodo = new Todo();
        when(todoRepositoryMock.save(todoToAdd)).thenReturn(savedTodo);

        // Act
        Todo result = todoService.save(todoToAdd);

        // Assert
        assertThat(result).isEqualTo(savedTodo);
    }

    @Test
    public void testGetNotDoneTodos() {
        // Create a list of "not done" TODOs
        List<Todo> notDoneTodoList = Arrays.asList(
            Todo.builder().description("Task 1").status(Todo.Status.NOT_DONE).build(),
            Todo.builder().description("Task 2").status(Todo.Status.NOT_DONE).build()
        );

        // Create a Page object from the list (simulating pagination)
        Page<Todo> notDoneTodoPage = new PageImpl<>(notDoneTodoList);

        // Mock the repository to return the Page object
        when(todoRepositoryMock.findByDone(eq(false), any(Pageable.class))).thenReturn(notDoneTodoPage);

        Page<Todo> notDoneTodos = todoService.getNotDoneTodos(1, 10); // Assuming page 1 and 10 items per page

        // Assertions
        assertThat(notDoneTodos.getTotalElements()).isEqualTo(2);
        assertThat(notDoneTodos.getContent().get(0).getDescription()).isEqualTo("Task 1");
        assertThat(notDoneTodos.getContent().get(1).getDescription()).isEqualTo("Task 2");
    }

    @Test
    public void testGetTodoDetails() {
        // Arrange
        Long todoId = 1L;
        Todo todo = new Todo();
        when(todoRepositoryMock.findById(todoId)).thenReturn(Optional.of(todo));

        // Act
        Todo result = todoService.getTodoDetails(todoId);

        // Assert
        assertThat(result).isEqualTo(todo);
    }

    @Test
    public void testGetTodoDetailsNotFound() {
        // Arrange
        Long todoId = 1L;
        when(todoRepositoryMock.findById(todoId)).thenReturn(Optional.empty());

        // Act
        Todo result = todoService.getTodoDetails(todoId);

        // Assert
        assertThat(result).isNull();
    }


}
