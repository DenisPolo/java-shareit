package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:clear-database.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
public class UserControllerIntegrationTest {
    private URI url;
    private UserCreationDto userCreationDto1;
    private UserCreationDto userCreationDto2;

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void beforeEach() {
        url = URI.create("http://localhost:" + port + "/users");

        userCreationDto1 = new UserCreationDto(
                "mail1@yandex.ru",
                "User1Name"
        );

        userCreationDto2 = new UserCreationDto(
                "mail2@yandex.ru",
                "User2Name"
        );
    }

    @Test
    public void getAllUsers() {
        UserDto expectedUser1 = new UserDto(1L, "mail1@yandex.ru", "User1Name", LocalDateTime.now().toString());
        UserDto expectedUser2 = new UserDto(2L, "mail2@yandex.ru", "User2Name", LocalDateTime.now().toString());

        assertThat(this.restTemplate.postForObject(url, userCreationDto1, UserDto.class)).isEqualTo(expectedUser1);
        assertThat(this.restTemplate.postForObject(url, userCreationDto2, UserDto.class)).isEqualTo(expectedUser2);

        ResponseEntity<UserDto[]> getUsersResponse = restTemplate.getForEntity(url, UserDto[].class);
        List<UserDto> usersList = Arrays.asList(Objects.requireNonNull(getUsersResponse.getBody()));

        assertEquals(usersList.get(0), expectedUser1);
        assertEquals(usersList.get(1), expectedUser2);
    }

    @Test
    public void getUserById() {
        UserDto expectedUser1 = new UserDto(1L, "mail1@yandex.ru", "User1Name", LocalDateTime.now().toString());
        UserDto expectedUser2 = new UserDto(2L, "mail2@yandex.ru", "User2Name", LocalDateTime.now().toString());

        assertThat(this.restTemplate.postForObject(url, userCreationDto1, UserDto.class)).isEqualTo(expectedUser1);
        assertThat(this.restTemplate.postForObject(url, userCreationDto2, UserDto.class)).isEqualTo(expectedUser2);

        assertThat(restTemplate.getForEntity(url.resolve("/users/1"), UserDto.class).getBody()).isEqualTo(expectedUser1);
        assertThat(restTemplate.getForEntity(url.resolve("/users/2"), UserDto.class).getBody()).isEqualTo(expectedUser2);
    }

    @Test
    public void createUser() {
        UserDto expectedUser1 = new UserDto(1L, "mail1@yandex.ru", "User1Name", LocalDateTime.now().toString());

        assertThat(this.restTemplate.postForObject(url, userCreationDto1, UserDto.class)).isEqualTo(expectedUser1);
    }

    @Test
    public void deleteUser() {
        UserDto expectedUser1 = new UserDto(1L, "mail1@yandex.ru", "User1Name", LocalDateTime.now().toString());

        assertThat(this.restTemplate.postForObject(url, userCreationDto1, UserDto.class)).isEqualTo(expectedUser1);

        restTemplate.delete(url.resolve("/users/1"));

        assertThat(this.restTemplate.getForObject(url, String.class)).contains("[]");
    }
}
