package com.s2.todo.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s2.todo.backend.model.Todo;
import com.s2.todo.backend.repository.TodoRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    @Transactional
    public void testCreateTodo() throws Exception {
        Todo todoRequest = Todo.builder().description("New Task").status(Todo.Status.NOT_DONE).build();

        ResultActions resultActions = mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoRequest)))
            .andExpect(status().isCreated());

        // Extract the ID of the newly created Todo from the response
        // Deserialize the newly created Todo from the response content
        Todo createdTodo = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), Todo.class);

        // Assert that the Todo was created and its properties match the request
        assertThat(createdTodo).isNotNull();
        assertThat(createdTodo.getDescription()).isEqualTo(todoRequest.getDescription());
        assertThat(createdTodo.getStatus()).isEqualTo(todoRequest.getStatus());

    }
}
