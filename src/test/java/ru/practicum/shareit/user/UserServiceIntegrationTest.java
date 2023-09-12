package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:clear-database.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
public class UserServiceIntegrationTest {
    private User user1;
    private User user2;
    private User user3;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(null, "user1", "user1@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));
        user2 = new User(null, "user2", "user2@yandex.ru", LocalDateTime.of(2023, 2, 2, 12, 0));
        user3 = new User(null, "user3", "user3@yandex.ru", LocalDateTime.of(2023, 3, 3, 12, 0));
    }

    @Test
    void testGetAllUsers() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        final List<UserDto> actual = userService.getAllUsers();

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        final List<UserDto> expected = UserMapper.INSTANCE.mapToUserDto(List.of(user1, user2, user3));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testGetUserById() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        final UserDto actual = userService.getUserById(2L);

        user2.setId(2L);

        final UserDto expected = UserMapper.INSTANCE.mapToUserDto(user2);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testCreateUser() {
        final UserCreationDto userCreationDto = new UserCreationDto("userCreated", "userCreated@yandex.ru");

        final UserDto actual = userService.createUser(userCreationDto);
        final UserDto expected = UserMapper.INSTANCE
                .mapToUserDto(new User(1L, "userCreated", "userCreated@yandex.ru", LocalDateTime.now()));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateUser() {
        final UserCreationDto userCreationDto = new UserCreationDto("userUpdated", "userUpdated@yandex.ru");

        final UserDto actual1 = userService.createUser(new UserCreationDto("user", "use@yandex.ru"));
        final UserDto expected1 = userService.getUserById(1L);
        final UserDto actual2 = userService.updateUser(1L, userCreationDto);
        final UserDto expected2 = UserMapper.INSTANCE
                .mapToUserDto(new User(1L, "userUpdated", "userUpdated@yandex.ru", LocalDateTime.now()));

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    void testUpdateUserShouldReturnExceptionWhenUserWithEmailAlreadyExists() {
        final UserCreationDto userCreationDto = new UserCreationDto("user@yandex.ru", "user3");

        userService.createUser(new UserCreationDto("user@yandex.ru", "user"));
        userService.createUser(new UserCreationDto("user1@yandex.ru", "user1"));

        final AlreadyExistsException exception = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> userService.updateUser(2L, userCreationDto));

        assertNotNull(exception);
        assertEquals("Пользователь с Email: user@yandex.ru уже существует", exception.getMessage());
    }

    @Test
    void testDeleteUser() {
        final UserDto actual1 = userService.createUser(new UserCreationDto("userCreated", "userCreated@yandex.ru"));
        final UserDto expected1 = userService.getUserById(1L);
        final ResponseFormat actual2 = userService.deleteUser(1L);
        final ResponseFormat expected2 = new ResponseFormat("Пользователь с ID: 1 успешно удален", HttpStatus.OK);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertEquals(expected1, actual1);
        assertEquals(expected2.getStatus(), actual2.getStatus());
        assertEquals(expected2.getMessage(), actual2.getMessage());
    }
}