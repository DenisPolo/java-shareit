package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllUsers();

    Optional<User> getUserById(long userId);

    User createUser(User user);

    boolean deleteUser(long userId);
}