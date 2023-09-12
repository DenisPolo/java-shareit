package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.responseFormat.ResponseFormat;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemCreationDto itemCreationDto;
    private ItemWithBookingsDto itemWithBookingsDto;
    private ItemForItemRequestDto itemForItemRequestDto;
    private ItemDto itemDto;
    private CommentCreationDto commentCreationDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        itemCreationDto = new ItemCreationDto(
                1L,
                1L,
                "item_name",
                "item_description",
                true,
                null
        );

        itemWithBookingsDto = new ItemWithBookingsDto(
                1L,
                "item_name",
                "item_description",
                true,
                null,
                null,
                new ArrayList<>(),
                "2023.01.01 12:00:00"
        );

        itemForItemRequestDto = new ItemForItemRequestDto(
                1L,
                "item_name",
                "item_description",
                true,
                null,
                "2023.01.01 12:00:00"
        );

        itemDto = new ItemDto(
                1L,
                "item_name",
                "item_description",
                true,
                "2023.01.01 12:00:00"
        );

        commentCreationDto = new CommentCreationDto(
                "comment"
        );

        commentDto = new CommentDto(
                1L,
                "user_name",
                "comment",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0)
        );
    }

    @Test
    void testFindItems() throws Exception {
        when(itemService.findItems(1L, 0, 10)).thenReturn(List.of(itemWithBookingsDto));

        mvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingsDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingsDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingsDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemWithBookingsDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemWithBookingsDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemWithBookingsDto.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", is(itemWithBookingsDto.getComments())))
                .andExpect(jsonPath("$[0].creationDate", is(itemWithBookingsDto.getCreationDate())));
    }

    @Test
    void testFindItem() throws Exception {
        when(itemService.findItem(1L, 1L)).thenReturn(itemWithBookingsDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithBookingsDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithBookingsDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBookingsDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithBookingsDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemWithBookingsDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemWithBookingsDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemWithBookingsDto.getComments())))
                .andExpect(jsonPath("$.creationDate", is(itemWithBookingsDto.getCreationDate())));
    }

    @Test
    void testSearchItems() throws Exception {
        when(itemService.searchItems("item", 0, 10)).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=item&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingsDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingsDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingsDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemWithBookingsDto.getAvailable())))
                .andExpect(jsonPath("$[0].creationDate", is(itemWithBookingsDto.getCreationDate())));
    }

    @Test
    void testSaveItem() throws Exception {
        when(itemService.saveItem(1L, itemCreationDto)).thenReturn(itemForItemRequestDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemCreationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemForItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemForItemRequestDto.getName())))
                .andExpect(jsonPath("$.description", is(itemForItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemForItemRequestDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemForItemRequestDto.getRequestId())))
                .andExpect(jsonPath("$.creationDate", is(itemForItemRequestDto.getCreationDate())));
    }

    @Test
    void testPostComment() throws Exception {
        when(itemService.postComment(1L, 1L, commentCreationDto)).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentCreationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(1L, 1L, itemCreationDto)).thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemCreationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.creationDate", is(itemDto.getCreationDate())));
    }

    @Test
    void testDeleteItem() throws Exception {
        final ResponseFormat response = new ResponseFormat("Вещь с id: 1 успешно удалена", HttpStatus.OK);

        when(itemService.deleteItem(1L, 1L)).thenReturn(response);

        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.message", is(response.getMessage())));
    }
}