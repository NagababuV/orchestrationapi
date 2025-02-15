package com.OrchestrationAPI.mapper;

import com.OrchestrationAPI.dto.UserDto;
import com.OrchestrationAPI.entity.User;

public class UserMapper {
    public static User toEntity(UserDto userDto) {
        User user = new User();
       // user.setId(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setSsn(userDto.getSsn());
        return user;
    }

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setSsn(user.getSsn());
        return userDto;
    }
}
