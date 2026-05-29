package com.academy.eventhub.service;

import com.academy.eventhub.dto.UserResponseDto;
import com.academy.eventhub.dto.UserRoleUpdateDto;
import com.academy.eventhub.dto.UserUpdateDto;
import com.academy.eventhub.entity.User;

import java.util.List;

public interface UserService
{
    public UserResponseDto getUserById(Long id);
    public List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(Long id, UserUpdateDto inputDto);
    void deleteUser(Long id);

    UserResponseDto updateUserRole(Long id, UserRoleUpdateDto roleDto);

    UserResponseDto getUserByUsername(String username);

    public void banUser(Long id);


}
