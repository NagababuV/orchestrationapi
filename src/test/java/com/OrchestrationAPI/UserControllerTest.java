package com.OrchestrationAPI;

import com.OrchestrationAPI.controller.UserController;
import com.OrchestrationAPI.dto.UserDto;
import com.OrchestrationAPI.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)  // Load only UserController and its dependencies
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // Proper way to mock service in @WebMvcTest
    private UserService userService;

    private List<UserDto> mockUsers;

    @BeforeEach
    void setUp() {
        mockUsers = List.of(
                new UserDto(1L, "test1@example.com", "John", "Doe", "1234", "image1"),
                new UserDto(2L, "test2@example.com", "Jane", "Doe", "5678", "image2")
        );
    }

    @Test
    void testLoadUsers() throws Exception {
        mockMvc.perform(post("/api/users/load")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Users loaded into H2 DB"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(mockUsers.size()))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].email").value("test2@example.com"));
    }

    @Test
    void testSearchUsers() throws Exception {
        when(userService.searchUsers("John")).thenReturn(List.of(mockUsers.get(0)));

        mockMvc.perform(get("/api/users/search").param("query", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(mockUsers.get(0));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testGetUserByEmail() throws Exception {
        when(userService.getUserByEmail("test1@example.com")).thenReturn(mockUsers.get(0));

        mockMvc.perform(get("/api/users/email/test1@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test1@example.com"));
    }
}
