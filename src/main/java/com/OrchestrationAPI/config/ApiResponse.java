package com.OrchestrationAPI.config;

import com.OrchestrationAPI.dto.UserDto;

import java.util.List;

public class ApiResponse {
    private List<UserDto> users;

    // Getters and setters
    public List<UserDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserDto> users) {
        this.users = users;
    }
}
