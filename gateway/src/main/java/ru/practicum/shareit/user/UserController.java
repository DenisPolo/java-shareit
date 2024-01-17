package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreationDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Get user with userId={}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreationDto userCreationDto) {
        log.info("Create user with userName={}, userEmail={}", userCreationDto.getName(), userCreationDto.getEmail());
        return userClient.createUser(userCreationDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody UserCreationDto userCreationDto) {
        log.info("Update user with userName={}, userEmail={}", userCreationDto.getName(), userCreationDto.getEmail());
        return userClient.updateUser(userId, userCreationDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Delete user with userId={}", userId);
        return userClient.deleteUser(userId);
    }
}