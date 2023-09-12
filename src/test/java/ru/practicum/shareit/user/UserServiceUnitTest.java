package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    private final long id = 1L;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetAllUsersNormalCondition() {
        final User user1 = mock(User.class);
        final User user2 = mock(User.class);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        final List<UserDto> actual = userService.getAllUsers();

        assertNotNull(actual);
        assertEquals(UserMapper.INSTANCE.mapToUserDto(List.of(user1, user2)), actual);
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserByIdNormalCondition() {
        final User user = mock(User.class);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        final UserDto actual = userService.getUserById(id);

        assertNotNull(actual);
        assertEquals(UserMapper.INSTANCE.mapToUserDto(user), actual);
        verify(userRepository).findById(id);
    }

    @Test
    void testCreateUserNormalCondition() {
        final UserCreationDto userCreationDto = mock(UserCreationDto.class);
        User user = UserMapper.INSTANCE.mapToNewUser(userCreationDto);

        when(userRepository.save(user)).thenReturn(user);

        final UserDto actual = userService.createUser(userCreationDto);

        assertNotNull(actual);
        assertEquals(UserMapper.INSTANCE.mapToUserDto(user), actual);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserNormalCondition() {
        final UserCreationDto userCreationDto = mock(UserCreationDto.class);
        final User updatableUser = new User();

        when(userCreationDto.getEmail()).thenReturn("user@email");
        when(userCreationDto.getName()).thenReturn("user_name");

        when(userRepository.findById(id)).thenReturn(Optional.of(updatableUser));
        when(userRepository.save(updatableUser)).thenReturn(updatableUser);

        final UserDto actual = userService.updateUser(id, userCreationDto);

        assertNotNull(actual);
        assertEquals(UserMapper.INSTANCE.mapToUserDto(updatableUser), actual);
        verify(userRepository).save(updatableUser);
    }

    @Test
    void testDeleteUserNormalCondition() {
        final User user = mock(User.class);

        when(userRepository.findById(id)).thenReturn(Optional.of(user)).thenReturn(Optional.empty());

        final ResponseFormat actual = userService.deleteUser(id);

        assertNotNull(actual);
        assertEquals(HttpStatus.OK, actual.getStatus());
        assertEquals("Пользователь с ID: 1 успешно удален", actual.getMessage());
        verify(userRepository, times(2)).findById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void testUpdateUserThrowsNotFoundExceptionWhenUserNotExists() {
        final UserCreationDto userCreationDto = mock(UserCreationDto.class);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(id, userCreationDto));

        Assertions.assertEquals("Пользователя с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testUpdateUserThrowsBadRequestExceptionWhenEmptyFieldsNameAndEmail() {
        final UserCreationDto userCreationDto = mock(UserCreationDto.class);
        final User updatableUser = new User();

        when(userRepository.findById(id)).thenReturn(Optional.of(updatableUser));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> userService.updateUser(id, userCreationDto));

        Assertions.assertEquals("Выполнен запрос с пустыми полями email и name", exception.getMessage());
    }

    @Test
    void testUpdateUserThrowsAlreadyExistsExceptionWhenExistsUserWithSameEmail() {
        final UserCreationDto userCreationDto = mock(UserCreationDto.class);
        final User user = mock(User.class);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userCreationDto.getEmail()).thenReturn("user@email");
        when(user.getId()).thenReturn(2L);
        when(user.getEmail()).thenReturn("user@email");

        final AlreadyExistsException exception = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> userService.updateUser(id, userCreationDto));

        Assertions.assertEquals("Пользователь с Email: user@email уже существует", exception.getMessage());
    }

    @Test
    void testDeleteUserThrowsBadRequestExceptionWhenUserDidNotDelete() {
        final User user = mock(User.class);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> userService.deleteUser(id));

        Assertions.assertEquals("Неизвестная ошибка. Пользователя с ID: 1 удалить не удалось", exception.getMessage());
    }
}