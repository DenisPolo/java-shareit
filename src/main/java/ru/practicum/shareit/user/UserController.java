package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ResponseFormat;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping()
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return service.getUserById(userId);
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        return service.createUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User user) {
        return service.updateUser(userId, user.getEmail(), user.getName());
    }

    @DeleteMapping("/{userId}")
    public ResponseFormat deleteUser(@PathVariable Long userId) {
        return service.deleteUser(userId);
    }
}