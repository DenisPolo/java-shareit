package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping()
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok().body(service.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(service.getUserById(userId));
    }

    @PostMapping()
    public ResponseEntity createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok().body(service.createUser(user));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity updateUser(@PathVariable Long userId, @RequestBody User user) {
        return ResponseEntity.ok().body(service.updateUser(userId, user.getEmail(), user.getName()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok().body(service.deleteUser(userId));
    }
}