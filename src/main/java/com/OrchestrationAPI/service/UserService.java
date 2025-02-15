package com.OrchestrationAPI.service;


import com.OrchestrationAPI.dto.UserDto;

import java.util.List;

public interface UserService {
    void loadUsersFromExternalApi();
    List<UserDto> getAllUsers();
    List<UserDto> searchUsers(String query);
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
}
