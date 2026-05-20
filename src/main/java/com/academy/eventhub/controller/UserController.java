package com.academy.eventhub.controller;

import com.academy.eventhub.dto.UserResponseDto;
import com.academy.eventhub.dto.UserUpdateDto;
import com.academy.eventhub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController
{
    private final UserService userService;

    @Autowired
    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers()
    {
        List<UserResponseDto> foundUsers = userService.getAllUsers();
        return ResponseEntity.ok(foundUsers);
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id)
    {
        UserResponseDto foundUser = userService.getUserById(id);
        return ResponseEntity.ok(foundUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
                                                      @Valid @RequestBody UserUpdateDto inputDto)
    {
        UserResponseDto updateUser = userService.updateUser(id, inputDto);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id)
    {
        userService.deleteUser(id);
        // Ritorna uno stato HTTP 204 No Content (operazione riuscita, nessun corpo)
        return ResponseEntity.noContent().build();
    }

}
