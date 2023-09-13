package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.UserRepository;
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
public class BookingServiceIntegrationTest {
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingServiceImpl bookingService;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(null, "user1", "user1@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));
        user2 = new User(null, "user2", "user2@yandex.ru", LocalDateTime.of(2023, 2, 2, 12, 0));
        user3 = new User(null, "user3", "user3@yandex.ru", LocalDateTime.of(2023, 3, 3, 12, 0));
        user4 = new User(null, "user4", "user4@yandex.ru", LocalDateTime.of(2023, 4, 4, 12, 0));
        item1 = new Item(null, user1, "item1", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));
        item2 = new Item(null, user2, "item2", "secondItem", true, null, LocalDateTime.of(2023, 2, 2, 20, 0));
        booking1 = new Booking(null, LocalDateTime.of(2023, 1, 1, 22, 0), LocalDateTime.of(2023, 1, 2, 22, 0),
                user3, item1, BookingStatus.APPROVED, LocalDateTime.of(2023, 1, 1, 22, 0));
        booking2 = new Booking(null, LocalDateTime.of(2023, 1, 3, 22, 0), LocalDateTime.of(2023, 1, 4, 22, 0),
                user3, item2, BookingStatus.APPROVED, LocalDateTime.of(2023, 1, 4, 22, 0));
        booking3 = new Booking(null, LocalDateTime.of(2023, 1, 5, 22, 0), LocalDateTime.of(2023, 1, 6, 22, 0),
                user4, item1, BookingStatus.APPROVED, LocalDateTime.of(2023, 1, 5, 22, 0));
        booking4 = new Booking(null, LocalDateTime.of(2023, 1, 7, 22, 0), LocalDateTime.of(2023, 1, 8, 22, 0),
                user4, item2, BookingStatus.APPROVED, LocalDateTime.of(2023, 1, 7, 22, 0));
    }

    @Test
    void testFindBookingsForUserAll() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        itemRepository.save(item1);
        itemRepository.save(item2);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);

        final List<BookingDto> actual1 = bookingService.findBookingsForUser(3L, "ALL", 0, 10);
        final List<BookingDto> actual2 = bookingService.findBookingsForUser(4L, "ALL", 0, 10);

        booking1.setId(1L);
        booking2.setId(2L);
        booking3.setId(3L);
        booking4.setId(4L);

        final List<BookingDto> expected1 = BookingMapper.INSTANCE.mapToBookingDto(List.of(booking2, booking1));
        final List<BookingDto> expected2 = BookingMapper.INSTANCE.mapToBookingDto(List.of(booking4, booking3));

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    void testFindBookingsForOwnerAll() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        itemRepository.save(item1);
        itemRepository.save(item2);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);

        final List<BookingDto> actual1 = bookingService.findBookingsForOwner(1L, "ALL", 0, 10);
        final List<BookingDto> actual2 = bookingService.findBookingsForOwner(2L, "ALL", 0, 10);

        booking1.setId(1L);
        booking2.setId(2L);
        booking3.setId(3L);
        booking4.setId(4L);

        final List<BookingDto> expected1 = BookingMapper.INSTANCE.mapToBookingDto(List.of(booking3, booking1));
        final List<BookingDto> expected2 = BookingMapper.INSTANCE.mapToBookingDto(List.of(booking4, booking2));

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    void testFindBooking() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        itemRepository.save(item1);
        itemRepository.save(item2);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        final BookingDto actual1 = bookingService.findBooking(1L, 1L);
        final BookingDto actual2 = bookingService.findBooking(3L, 1L);
        final BookingDto actual3 = bookingService.findBooking(2L, 2L);

        booking1.setId(1L);
        booking2.setId(2L);

        final BookingDto expected1 = BookingMapper.INSTANCE.mapToBookingDto(booking1);
        final BookingDto expected2 = BookingMapper.INSTANCE.mapToBookingDto(booking2);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotNull(actual3);
        assertEquals(expected1, actual1);
        assertEquals(expected1, actual2);
        assertEquals(expected2, actual3);
    }

    @Test
    void testCreateBooking() {
        final LocalDateTime time = LocalDateTime.now();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        itemRepository.save(item1);
        itemRepository.save(item2);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);

        final BookingDto actual = bookingService.createBooking(3L, new BookingCreationDto(null,
                time.plusDays(1), time.plusDays(2), 3L, 1L, null));
        final BookingDto expected = BookingMapper.INSTANCE.mapToBookingDto(new Booking(5L,
                time.plusDays(1), time.plusDays(2),
                user3, item1, BookingStatus.WAITING, LocalDateTime.now()));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateBooking() {
        booking1.setStatus(BookingStatus.WAITING);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        itemRepository.save(item1);
        bookingRepository.save(booking1);

        final BookingDto actual1 = bookingService.findBooking(1L, 1L);
        final BookingDto expected1 = BookingMapper.INSTANCE.mapToBookingDto(booking1);
        final BookingDto actual2 = bookingService.updateBooking(1L, 1L, true);
        final BookingDto expected2 = BookingMapper.INSTANCE.mapToBookingDto(booking1);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    void testDeleteBooking() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        itemRepository.save(item1);
        bookingRepository.save(booking1);

        final BookingDto actual1 = bookingService.findBooking(1L, 1L);
        final BookingDto expected1 = BookingMapper.INSTANCE.mapToBookingDto(booking1);
        final ResponseFormat actual2 = bookingService.deleteBooking(1L, 1L);
        final ResponseFormat expected2 = new ResponseFormat("Запрос бронирования с id: 1 успешно удален",
                HttpStatus.OK);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertEquals(expected1, actual1);
        assertEquals(expected2.getStatus(), actual2.getStatus());
        assertEquals(expected2.getMessage(), actual2.getMessage());
    }
}
