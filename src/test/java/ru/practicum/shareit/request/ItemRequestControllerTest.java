package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.responseFormat.ResponseFormat;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestServiceImpl itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());

        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        ItemForItemRequestDto itemForItemRequestDto = new ItemForItemRequestDto(
                1L,
                "item_name",
                "item_description",
                true,
                null,
                "2023.01.02 12:00:00"
        );

        itemRequest = new ItemRequest(
                1L,
                null,
                "item_request_description",
                Instant.now().minusSeconds(60 * 60 * 24)
        );

        itemRequestDto = new ItemRequestDto(
                1L,
                "item_request_description",
                "2023.01.03 12:00:00",
                List.of(itemForItemRequestDto)
        );
    }

    @Test
    void testFindUsersItemRequests() throws Exception {
        when(itemRequestService.findUsersItemRequests(1L)).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDto.getItems().get(0).getDescription())));
    }

    @Test
    void testFindItemRequests() throws Exception {
        when(itemRequestService.findItemRequests(1L, 0, 10)).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDto.getItems().get(0).getDescription())));
    }

    @Test
    void testFindItemRequest() throws Exception {
        when(itemRequestService.findItemRequest(1L, 1L)).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated())))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].description", is(itemRequestDto.getItems().get(0).getDescription())));
    }

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(1L, itemRequest)).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated())))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].description", is(itemRequestDto.getItems().get(0).getDescription())));
    }

    @Test
    void testDeleteItemRequest() throws Exception {
        final ResponseFormat response = new ResponseFormat("Запрос вещи с id: 1 успешно удален", HttpStatus.OK);

        when(itemRequestService.deleteItemRequest(1L, 1L)).thenReturn(response);

        mvc.perform(delete("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.message", is(response.getMessage())));
    }
}