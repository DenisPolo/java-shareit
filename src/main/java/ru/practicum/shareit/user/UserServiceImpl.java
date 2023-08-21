package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        log.info("Запрос списка всех пользователей");

        List<User> users = repository.findAll();

        return UserMapper.mapToUserDto(users);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long userId) {
        log.info("Запрос пользователея с ID: " + userId);

        User user = getUserIfExists(userId);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto createUser(UserCreationDto userCreationDto) {
        log.info("Запрос добавления нового пользователя с Email: " + userCreationDto.getEmail() +
                ", Name: " + userCreationDto.getName());

        User user = UserMapper.mapToNewUser(userCreationDto);

        return UserMapper.mapToUserDto(repository.save(user));
    }

    @Override
    public UserDto updateUser(long userId, UserCreationDto userCreationDto) {
        log.info("Запрос обноваления данных пользователя с ID: " + userId);

        User updatableUser = getUserIfExists(userId);

        if ((userCreationDto.getEmail() == null) && (userCreationDto.getName() == null)) {
            String message = "Выполнен запрос с пустыми полями email и name";

            log.info(message);

            throw new BadRequestException(message);
        }

        if (repository.findAll().stream().anyMatch(u -> (u.getId() != userId)
                && u.getEmail().equals(userCreationDto.getEmail()))) {
            String message = "Пользователь с Email: " + userCreationDto.getEmail() + " уже существует";

            log.info(message);

            throw new AlreadyExistsException(message);
        }

        if (userCreationDto.getEmail() != null) {
            updatableUser.setEmail(userCreationDto.getEmail());
        }

        if (userCreationDto.getName() != null) {
            updatableUser.setName(userCreationDto.getName());
        }

        return UserMapper.mapToUserDto(repository.save(updatableUser));
    }

    @Override
    public ResponseFormat deleteUser(long userId) {
        getUserIfExists(userId);

        repository.deleteById(userId);

        if (repository.findById(userId).isEmpty()) {
            String message = "Пользователь с ID: " + userId + " успешно удален";

            log.info(message);

            return new ResponseFormat(message, HttpStatus.OK);
        } else {
            String message = "Неизвестная ошибка. Пользователя с ID: " + userId + " удалить не удалось";

            log.warn(message);

            throw new BadRequestException(message);
        }
    }

    private User getUserIfExists(long userId) {
        Optional<User> user = repository.findById(userId);

        if (user.isEmpty()) {
            String message = "Пользователя с ID: " + userId + " не существует";

            log.info(message);

            throw new NotFoundException(message);
        }

        return user.get();
    }
}