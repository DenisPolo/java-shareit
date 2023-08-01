package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    long userId;
    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return users.stream().filter(u -> u.getId() == userId).findFirst();
    }

    @Override
    public User createUser(User user) {
        userId++;
        user.setId(userId);
        users.add(user);
        return user;
    }

    @Override
    public boolean deleteUser(long userId) {
        return users.remove(users.stream().filter(u -> u.getId() == userId).findFirst().get());
    }
}