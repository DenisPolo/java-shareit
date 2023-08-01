package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ResponseFormat;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        log.info("Запрос списка всех пользователей");
        return repository.getAllUsers();
    }

    @Override
    public User getUserById(long userId) {
        log.info("Запрос пользователея с ID: " + userId);
        checkUserExists(userId);
        return repository.getUserById(userId).get();
    }

    @Override
    public User createUser(User user) {
        log.info("Запрос добавления нового пользователя с Email: " + user.getEmail() + ", Name: " + user.getName());
        checkUserEmail(user.getEmail());
        return repository.createUser(user);
    }

    @Override
    public User updateUser(long userId, String email, String name) {
        log.info("Запрос обноваления данных пользователя с ID: " + userId);
        checkUserExists(userId);
        if ((email == null) && (name == null)) {
            String message = "Выполнен запрос с пустыми полями email и name";
            log.info(message);
            throw new RuntimeException();
        }
        if (repository.getAllUsers().stream().anyMatch(u -> (u.getId() != userId) && u.getEmail().equals(email))) {
            String message = "Пользователя с Email: " + email + " уже существует";
            log.info(message);
            throw new AlreadyExistsException(message);
        }
        if (name != null) {
            repository.getUserById(userId).get().setName(name);
        }
        if (email != null) {
            repository.getUserById(userId).get().setEmail(email);
        }
        return repository.getUserById(userId).get();
    }

    @Override
    public ResponseFormat deleteUser(long userId) {
        checkUserExists(userId);
        if (repository.deleteUser(userId)) {
            String message = "Пользователь с ID: " + userId + " успешно удален";
            log.info(message);
            return new ResponseFormat(message, HttpStatus.OK);
        } else {
            String message = "Неизвестная ошибка. Пользователя с ID: " + userId + " удалить не удалось";
            log.warn(message);
            throw new RuntimeException();
        }
    }

    private void checkUserExists(long userId) {
        if (repository.getUserById(userId).isEmpty()) {
            String message = "Пользователя с ID: " + userId + " не существует";
            log.info(message);
            throw new NotFoundException(message);
        }
    }

    private void checkUserEmail(String email) {
        if (repository.getAllUsers().stream().anyMatch(u -> u.getEmail().equals(email))) {
            String message = "Пользователя с Email: " + email + " уже существует";
            log.info(message);
            throw new AlreadyExistsException(message);
        }
    }
}