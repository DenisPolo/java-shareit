package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
public class ItemControllerIntegrationTest {
    private URI url;
    private ItemCreationDto itemCreationDto1;
    private ItemCreationDto itemCreationDto2;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @BeforeEach
    public void beforeEach() {
        url = URI.create("http://localhost:" + port + "/items");

        UserCreationDto userCreationDto1 = new UserCreationDto("mail1@yandex.ru", "User1Name");
        UserCreationDto userCreationDto2 = new UserCreationDto("mail2@yandex.ru", "User2Name");

        itemCreationDto1 = new ItemCreationDto(
                null,
                1L,
                "item1",
                "first item",
                true,
                null
        );

        itemCreationDto2 = new ItemCreationDto(
                null,
                2L,
                "item2",
                "second item",
                true,
                null
        );

        restTemplate.postForObject(url.resolve("/users"), userCreationDto1, UserDto.class);
        restTemplate.postForObject(url.resolve("/users"), userCreationDto2, UserDto.class);
    }

    @Test
    public void findAllItems() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        System.out.println("************************: " + headers.getValuesAsList("X-Sharer-User-Id"));

        ItemWithBookingsDto expectedItem1 = new ItemWithBookingsDto(1L, "item1", "first item", true, null, null,
                null, LocalDateTime.now().format(formatter));
        ItemWithBookingsDto expectedItem2 = new ItemWithBookingsDto(2L, "item2", "second item", true, null, null,
                null, LocalDateTime.now().format(formatter));

        assertThat(this.restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(itemCreationDto1, headers),
                ItemWithBookingsDto.class).getBody()).isEqualTo(expectedItem1);
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        assertThat(this.restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(itemCreationDto2, headers),
                ItemWithBookingsDto.class).getBody()).isEqualTo(expectedItem2);

        ResponseEntity<ItemWithBookingsDto[]> getItemsResponse = restTemplate.getForEntity(url, ItemWithBookingsDto[].class);
        List<ItemWithBookingsDto> itemsList = Arrays.asList(Objects.requireNonNull(getItemsResponse.getBody()));

        assertEquals(itemsList.get(0), expectedItem1);
        assertEquals(itemsList.get(1), expectedItem2);
    }

    @Test
    public void findUsersItems() {
        ItemWithBookingsDto expectedItem1 = new ItemWithBookingsDto(1L, "item1", "first item", true, null, null,
                null, LocalDateTime.now().format(formatter));
        ItemWithBookingsDto expectedItem2 = new ItemWithBookingsDto(2L, "item2", "second item", true, null, null,
                null, LocalDateTime.now().format(formatter));

        assertThat(this.restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(itemCreationDto1, getHeaders(1)),
                ItemWithBookingsDto.class).getBody()).isEqualTo(expectedItem1);
        assertThat(this.restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(itemCreationDto2, getHeaders(2)),
                ItemWithBookingsDto.class).getBody()).isEqualTo(expectedItem2);

        ResponseEntity<ItemWithBookingsDto[]> getItemsResponse = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(getHeaders(1)), ItemWithBookingsDto[].class);
        List<ItemWithBookingsDto> itemsList = Arrays.asList(Objects.requireNonNull(getItemsResponse.getBody()));

        assertThat(itemsList).size().isEqualTo(1);
        assertEquals(itemsList.get(0), expectedItem1);
    }

    @Test
    public void findItemsEmpty() {
        ResponseEntity<ItemWithBookingsDto[]> getItemsResponse = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(getHeaders(1)), ItemWithBookingsDto[].class);
        List<ItemWithBookingsDto> itemsList = Arrays.asList(Objects.requireNonNull(getItemsResponse.getBody()));

        assertThat(itemsList).size().isEqualTo(0);
    }

    private HttpHeaders getHeaders(Integer value) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", value.toString());
        return headers;
    }
}