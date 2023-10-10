package ru.practicum.shareit.user;

import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto createUser(UserCreationDto userCreationDto);

    UserDto updateUser(long userId, UserCreationDto userCreationDto);

    ResponseFormat deleteUser(long userId);
}