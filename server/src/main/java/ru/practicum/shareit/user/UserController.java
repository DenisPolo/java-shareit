package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok().body(service.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(service.getUserById(userId));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserCreationDto userCreationDto) {
        return ResponseEntity.ok().body(service.createUser(userCreationDto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId,
                                              @RequestBody UserCreationDto userCreationDto) {
        return ResponseEntity.ok().body(service.updateUser(userId, userCreationDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseFormat> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok().body(service.deleteUser(userId));
    }
}