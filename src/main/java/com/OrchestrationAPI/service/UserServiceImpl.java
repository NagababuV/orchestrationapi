package com.OrchestrationAPI.service;

import com.OrchestrationAPI.config.ApiResponse;
import com.OrchestrationAPI.dto.UserDto;
import com.OrchestrationAPI.entity.User;
import com.OrchestrationAPI.exception.UserNotFoundException;
import com.OrchestrationAPI.mapper.UserMapper;
import com.OrchestrationAPI.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public UserServiceImpl(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    private static final String EXTERNAL_API_URL = "https://dummyjson.com/users";
    private static final String USER_SERVICE = "userService";

    @Override
    @Transactional
    @PostConstruct
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fallbackLoadUsers")
    @Retry(name = USER_SERVICE)
    public void loadUsersFromExternalApi() {
        log.info("Fetching users from external API");

        ApiResponse response = restTemplate.getForObject(EXTERNAL_API_URL, ApiResponse.class);

        if (response != null && response.getUsers() != null) {
            List<User> userEntities = new ArrayList<>();
            for (UserDto userDto : response.getUsers()) {
                userEntities.add(UserMapper.toEntity(userDto));
            }
            userRepository.saveAll(userEntities);
            log.info("Users loaded into H2 DB");
        }
    }

    public void fallbackLoadUsers(Exception e) {
        log.error("Fallback: Failed to load users from external API - {}", e.getMessage());
    }

    @Override
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fallbackGetAllUsers")
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(UserMapper.toDto(user));
        }
        return userDtos;
    }

    public List<UserDto> fallbackGetAllUsers(Exception e) {
        log.error("Fallback: Unable to fetch users - {}", e.getMessage());
        return new ArrayList<>();
    }

    @Override
    public List<UserDto> searchUsers(String query) {
        List<User> users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrSsnContainingIgnoreCase(query, query, query);
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(UserMapper.toDto(user));
        }
        return userDtos;
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserMapper.toDto(user.get());
        } else {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
    }

    @Override
    public UserDto getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return UserMapper.toDto(user.get());
        } else {
            throw new UserNotFoundException("User not found with Email: " + email);
        }
    }
}
