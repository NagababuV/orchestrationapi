package com.OrchestrationAPI;

import com.OrchestrationAPI.config.ApiResponse;
import com.OrchestrationAPI.dto.UserDto;
import com.OrchestrationAPI.entity.User;
import com.OrchestrationAPI.exception.UserNotFoundException;
import com.OrchestrationAPI.mapper.UserMapper;
import com.OrchestrationAPI.repository.UserRepository;
import com.OrchestrationAPI.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testLoadUsersFromExternalApi() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setUsers(Arrays.asList(new UserDto(1L, "test@example.com", "John", "Doe", "1234", "image.jpg")));

        when(restTemplate.getForObject(anyString(), eq(ApiResponse.class))).thenReturn(apiResponse);
        when(userRepository.saveAll(anyList())).thenReturn(null);

        userService.loadUsersFromExternalApi();

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ApiResponse.class));
        verify(userRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(new User(1L, "test@example.com", "John", "Doe", "1234", "image.jpg"));
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
    }

    @Test
    void testSearchUsers() {
        List<User> users = Arrays.asList(new User(1L, "test@example.com", "John", "Doe", "1234", "image.jpg"));
        when(userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrSsnContainingIgnoreCase(any(), any(), any()))
                .thenReturn(users);

        List<UserDto> result = userService.searchUsers("John");

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void testGetUserById_Success() {
        User user = new User(1L, "test@example.com", "John", "Doe", "1234", "image.jpg");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testGetUserByEmail_Success() {
        User user = new User(1L, "test@example.com", "John", "Doe", "1234", "image.jpg");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDto result = userService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("test@example.com"));
    }
}
