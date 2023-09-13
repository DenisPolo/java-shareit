package ru.practicum.shareit.exception;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:clear-database.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
public class ExceptionHandlerTest {
    private URI url;
    private UserCreationDto user1;
    private UserCreationDto user2;

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @BeforeEach
    public void beforeEach() {
        url = URI.create("http://localhost:" + port + "/users");

        user1 = new UserCreationDto(
                "",
                "User1Name"
        );

        user2 = new UserCreationDto(
                "mail@yandex.ru",
                "User2Name"
        );
    }

    @Test
    public void createUser_shouldReturnBadRequest_whenEmptyUserNameAndEmail() {
        ResponseEntity<ErrorResponseFormat> postUserResponse = restTemplate.postForEntity(url, user1,
                ErrorResponseFormat.class);

        assertSame(postUserResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(postUserResponse.getBody()).getError(), "Email не должен быть пустым");
    }

    @Test
    public void searchItems_shouldReturnBadRequest_whenEmptySearchText() {

        ResponseEntity<ErrorResponseFormat> response = restTemplate
                .getForEntity(url.resolve("/items/search?from=0&size=10"), ErrorResponseFormat.class);

        assertSame(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(response.getBody()).getError(), "Отсутствует параметр запроса");
    }

    @Test
    public void createUser_shouldReturnInternalServerError_whenUserHasSameEmail() {
        restTemplate.postForEntity(url, user2, UserDto.class);
        ResponseEntity<ErrorResponseFormat> response = restTemplate.postForEntity(url, user2, ErrorResponseFormat.class);

        assertSame(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assert (Objects.requireNonNull(response.getBody()).getError()
                .contains("org.hibernate.exception.ConstraintViolationException"));
    }

    @Test
    public void getUserById_shouldReturnNotFoundException_whenUserNotExists() {
        ResponseEntity<ErrorResponseFormat> response = restTemplate.getForEntity(url.resolve("/users/1"),
                ErrorResponseFormat.class);

        assertSame(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(Objects.requireNonNull(response.getBody()).getError(), "Пользователя с ID: 1 не существует");
    }

    @Test
    public void createUser_shouldReturnBadRequest_whenNameIsBlanc() {
        user1.setName(" ");

        ResponseEntity<ErrorResponseFormat> response = restTemplate.postForEntity(url, user1, ErrorResponseFormat.class);

        assertSame(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(response.getBody()).getError(), ("Имя не должно быть пустым"));
    }

    @Test
    public void createUser_shouldReturnBadRequest_whenEmailDoesNotMatchEmailFormat() {
        user1.setEmail("mail");

        ResponseEntity<ErrorResponseFormat> response = restTemplate.postForEntity(url, user1, ErrorResponseFormat.class);

        assertSame(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assert (Objects.requireNonNull(response.getBody()).getError()
                .contains("Email не соответстует формату адреса электронной почты"));
    }
}