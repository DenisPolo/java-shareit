package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:clear-database.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
public class BookingRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void testFindBookingsForItemShouldReturnEmptyListWhenEmptyDatabase() {
        List<Booking> bookings = bookingRepository.findBookingsForItem(1L);

        assertThat(bookings).isEmpty();
    }

    @Test
    public void testFindBookingsForItemShouldReturnListWithOneBookingWhenBookingExists() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));

        userRepository.save(user);

        Item item = new Item(null, user, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        Booking booking = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                user, item, BookingStatus.APPROVED, LocalDateTime.now().plusDays(1));

        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingsForItem(1L);

        Assertions.assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindBookingsForItemInShouldReturnListWithTwoBookingsWhenBookingsExist() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));

        userRepository.save(user);

        Item item = new Item(null, user, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        Booking booking1 = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                user, item, BookingStatus.APPROVED, LocalDateTime.now().plusDays(1));

        Booking booking2 = new Booking(null, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4),
                user, item, BookingStatus.APPROVED, LocalDateTime.now().plusDays(3));

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingsForItem(1L);

        Assertions.assertEquals(booking1, bookings.get(0));
        Assertions.assertEquals(booking2, bookings.get(1));
    }

    @Test
    public void testFindBookingsForUserShouldReturnListWithTwoBookingsWhenBookingsExist() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));
        User owner = new User(null, "owner", "owner@yandex.ru", LocalDateTime.of(2023, 1, 1, 13, 0));

        userRepository.save(user);
        userRepository.save(owner);

        Item item = new Item(null, owner, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        Booking booking1 = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                user, item, BookingStatus.APPROVED, LocalDateTime.now().plusDays(1));

        Booking booking2 = new Booking(null, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4),
                user, item, BookingStatus.APPROVED, LocalDateTime.now().plusDays(3));

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingsForUser(1L, PageRequest.of(0, 10));

        Assertions.assertEquals(booking2, bookings.get(0));
        Assertions.assertEquals(booking1, bookings.get(1));
    }

    @Test
    public void testFindBookingsByStatusForUserShouldReturnListWithOneApprovedBookingWhenBookingsWithStatusExists() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));
        User owner = new User(null, "owner", "owner@yandex.ru", LocalDateTime.of(2023, 1, 1, 13, 0));

        userRepository.save(user);
        userRepository.save(owner);

        Item item = new Item(null, owner, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        Booking booking1 = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                user, item, BookingStatus.REJECTED, LocalDateTime.now().plusDays(1));

        Booking booking2 = new Booking(null, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4),
                user, item, BookingStatus.APPROVED, LocalDateTime.now().plusDays(3));

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingsByStatusForUser(1L, BookingStatus.APPROVED,
                PageRequest.of(0, 10));

        Assertions.assertEquals(booking2, bookings.get(0));
    }

    @Test
    public void testFindBookingsForOwnerShouldReturnListWithTwoBookingsWhenBookingsExist() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));
        User owner = new User(null, "owner", "owner@yandex.ru", LocalDateTime.of(2023, 1, 1, 13, 0));

        userRepository.save(user);
        userRepository.save(owner);

        Item item = new Item(null, owner, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        Booking booking1 = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                user, item, BookingStatus.REJECTED, LocalDateTime.now().plusDays(1));

        Booking booking2 = new Booking(null, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4),
                user, item, BookingStatus.APPROVED, LocalDateTime.now().plusDays(3));

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingsForOwner(2L, PageRequest.of(0, 10));

        Assertions.assertEquals(booking2, bookings.get(0));
        Assertions.assertEquals(booking1, bookings.get(1));
    }

    @Test
    public void testFindBookingsByStatusForOwnerShouldReturnListWithOneApprovedBookingWhenBookingsWithStatusExists() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));
        User owner = new User(null, "owner", "owner@yandex.ru", LocalDateTime.of(2023, 1, 1, 13, 0));

        userRepository.save(user);
        userRepository.save(owner);

        Item item = new Item(null, owner, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        Booking booking1 = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                user, item, BookingStatus.REJECTED, LocalDateTime.now().plusDays(1));

        Booking booking2 = new Booking(null, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4),
                user, item, BookingStatus.APPROVED, LocalDateTime.now().plusDays(3));

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingsByStatusForOwner(2L, BookingStatus.APPROVED,
                PageRequest.of(0, 10));

        Assertions.assertEquals(booking2, bookings.get(0));
    }
}