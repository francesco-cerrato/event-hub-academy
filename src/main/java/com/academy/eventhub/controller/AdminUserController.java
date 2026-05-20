package com.academy.eventhub.controller;

import com.academy.eventhub.dto.UserResponseDto;
import com.academy.eventhub.dto.UserRoleUpdateDto;
import com.academy.eventhub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users") // Bloccato automaticamente da Spring Security
public class AdminUserController
{
    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService)
    {
        this.userService = userService;
    }


    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(@PathVariable Long id, @Valid @RequestBody UserRoleUpdateDto roleDto)
    {
        UserResponseDto updatedUser = userService.updateUserRole(id, roleDto);
        return ResponseEntity.ok(updatedUser);
    }
}
