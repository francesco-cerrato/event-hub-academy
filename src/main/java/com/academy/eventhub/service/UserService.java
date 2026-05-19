package com.academy.eventhub.service;

import com.academy.eventhub.dto.UserResponseDto;
import com.academy.eventhub.dto.UserUpdateDto;

import java.util.List;

public interface UserService
{
    public UserResponseDto getUserById(Long id);
    public List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(Long id, UserUpdateDto inputDto);
    void deleteUser(Long id);

}
