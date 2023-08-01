package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ResponseFormat;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(long userId);

    User createUser(User user);

    User updateUser(long userId, String email, String name);

    ResponseFormat deleteUser(long userId);
}