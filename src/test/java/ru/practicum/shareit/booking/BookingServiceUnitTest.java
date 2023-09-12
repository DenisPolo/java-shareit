package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
    private final long itemId = 1L;
    private final long userId = 1L;
    private final long bookingId = 1L;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void testFindBookingsForUserNormalConditionAllState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsForUser(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));

        final List<BookingDto> actual = bookingService.findBookingsForUser(userId, "ALL", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking1, booking2)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsForUser(userId, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForUserNormalConditionWaitingState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByStatusForUser(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));

        final List<BookingDto> actual = bookingService.findBookingsForUser(userId, "WAITING", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking1, booking2)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsByStatusForUser(userId, BookingStatus.WAITING, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForUserNormalConditionRejectedState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByStatusForUser(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));

        final List<BookingDto> actual = bookingService.findBookingsForUser(userId, "REJECTED", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking1, booking2)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsByStatusForUser(userId, BookingStatus.REJECTED, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForUserNormalConditionFutureState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final Booking booking3 = mock(Booking.class);
        final User user = mock(User.class);

        when(booking1.getStart()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking2.getStart()).thenReturn(LocalDateTime.now().plusDays(2));
        when(booking3.getStart()).thenReturn(LocalDateTime.now().plusDays(3));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsForUser(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2, booking3));

        final List<BookingDto> actual = bookingService.findBookingsForUser(userId, "FUTURE", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking2, booking3)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsForUser(userId, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForUserNormalConditionCurrentState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final Booking booking3 = mock(Booking.class);
        final User user = mock(User.class);

        when(booking1.getStart()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking1.getEnd()).thenReturn(LocalDateTime.now().plusDays(1));
        when(booking2.getStart()).thenReturn(LocalDateTime.now().minusDays(2));
        when(booking2.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(booking3.getStart()).thenReturn(LocalDateTime.now().plusDays(3));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsForUser(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2, booking3));

        final List<BookingDto> actual = bookingService.findBookingsForUser(userId, "CURRENT", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking1, booking2)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsForUser(userId, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForUserNormalConditionPastState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final Booking booking3 = mock(Booking.class);
        final User user = mock(User.class);

        when(booking1.getEnd()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking2.getEnd()).thenReturn(LocalDateTime.now().minusDays(2));
        when(booking3.getEnd()).thenReturn(LocalDateTime.now().plusDays(3));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsForUser(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2, booking3));

        final List<BookingDto> actual = bookingService.findBookingsForUser(userId, "PAST", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking1, booking2)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsForUser(userId, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForOwnerNormalConditionAllState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsForOwner(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));

        final List<BookingDto> actual = bookingService.findBookingsForOwner(userId, "ALL", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking1, booking2)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsForOwner(userId, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForOwnerNormalConditionWaitingState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByStatusForOwner(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));

        final List<BookingDto> actual = bookingService.findBookingsForOwner(userId, "WAITING", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking1, booking2)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsByStatusForOwner(userId, BookingStatus.WAITING, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForOwnerNormalConditionRejectedState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByStatusForOwner(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));

        final List<BookingDto> actual = bookingService.findBookingsForOwner(userId, "REJECTED", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking1, booking2)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsByStatusForOwner(userId, BookingStatus.REJECTED, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForOwnerNormalConditionFutureState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final Booking booking3 = mock(Booking.class);
        final User user = mock(User.class);

        when(booking1.getStart()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking2.getStart()).thenReturn(LocalDateTime.now().plusDays(2));
        when(booking3.getStart()).thenReturn(LocalDateTime.now().plusDays(3));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsForOwner(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2, booking3));

        final List<BookingDto> actual = bookingService.findBookingsForOwner(userId, "FUTURE", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking2, booking3)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsForOwner(userId, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForOwnerNormalConditionCurrentState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final Booking booking3 = mock(Booking.class);
        final User user = mock(User.class);

        when(booking1.getStart()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking1.getEnd()).thenReturn(LocalDateTime.now().plusDays(1));
        when(booking2.getStart()).thenReturn(LocalDateTime.now().minusDays(2));
        when(booking2.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(booking3.getStart()).thenReturn(LocalDateTime.now().plusDays(3));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsForOwner(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2, booking3));

        final List<BookingDto> actual = bookingService.findBookingsForOwner(userId, "CURRENT", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking1, booking2)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsForOwner(userId, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingsForOwnerNormalConditionPastState() {
        final Booking booking1 = mock(Booking.class);
        final Booking booking2 = mock(Booking.class);
        final Booking booking3 = mock(Booking.class);
        final User user = mock(User.class);

        when(booking1.getEnd()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking2.getEnd()).thenReturn(LocalDateTime.now().minusDays(2));
        when(booking3.getEnd()).thenReturn(LocalDateTime.now().plusDays(3));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsForOwner(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2, booking3));

        final List<BookingDto> actual = bookingService.findBookingsForOwner(userId, "PAST", 0, 10);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(List.of(booking1, booking2)), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsForOwner(userId, PageRequest.of(0, 10));
    }

    @Test
    void testFindBookingNormalCondition() {
        final Booking booking = mock(Booking.class);
        final Item item = mock(Item.class);
        final User owner = mock(User.class);

        when(booking.getItem()).thenReturn(item);
        when(item.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(userId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        final BookingDto actual = bookingService.findBooking(userId, bookingId);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(booking), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void testCreateBookingNormalCondition() {
        final BookingCreationDto bookingCreationDto = mock(BookingCreationDto.class);
        final User booker = mock(User.class);
        final User owner = mock(User.class);
        final Item item = mock(Item.class);
        Booking booking = BookingMapper.INSTANCE.mapToNewBooking(bookingCreationDto, booker, item);

        when(bookingCreationDto.getItemId()).thenReturn(itemId);
        when(bookingCreationDto.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(bookingCreationDto.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(owner.getId()).thenReturn(userId + 1);
        when(item.getOwner()).thenReturn(owner);
        when(item.getAvailable()).thenReturn(true);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsForItem(anyLong())).thenReturn(new ArrayList<>());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        final BookingDto actual = bookingService.createBooking(userId, bookingCreationDto);


        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(booking), actual);
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).findBookingsForItem(itemId);
    }

    @Test
    void testUpdateBookingNormalConditionApproved() {
        final User owner = mock(User.class);
        final Item item = mock(Item.class);
        final Booking updatedBooking = new Booking();
        updatedBooking.setItem(item);
        updatedBooking.setStatus(BookingStatus.WAITING);

        when(owner.getId()).thenReturn(userId);
        when(item.getOwner()).thenReturn(owner);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(updatedBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

        final BookingDto actual = bookingService.updateBooking(userId, bookingId, true);
        updatedBooking.setStatus(BookingStatus.APPROVED);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(updatedBooking), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testUpdateBookingNormalConditionRejected() {
        final User owner = mock(User.class);
        final Item item = mock(Item.class);
        final Booking updatedBooking = new Booking();
        updatedBooking.setItem(item);
        updatedBooking.setStatus(BookingStatus.WAITING);

        when(owner.getId()).thenReturn(userId);
        when(item.getOwner()).thenReturn(owner);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(updatedBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

        final BookingDto actual = bookingService.updateBooking(userId, bookingId, false);
        updatedBooking.setStatus(BookingStatus.REJECTED);

        assertNotNull(actual);
        assertEquals(BookingMapper.INSTANCE.mapToBookingDto(updatedBooking), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testDeleteBookingNormalCondition() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final Booking booking = mock(Booking.class);

        when(user.getId()).thenReturn(userId);
        when(item.getOwner()).thenReturn(user);
        when(booking.getItem()).thenReturn(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking)).thenReturn(Optional.empty());

        final ResponseFormat actual = bookingService.deleteBooking(userId, bookingId);

        assertNotNull(actual);
        assertEquals(HttpStatus.OK, actual.getStatus());
        assertEquals("Запрос бронирования с id: 1 успешно удален", actual.getMessage());
        verify(userRepository).findById(userId);
        verify(bookingRepository, times(2)).findById(bookingId);
        verify(bookingRepository).deleteById(bookingId);
    }

    @Test
    void testFindBookingsThrowsBadRequestExceptionWhenPageFromLessThanZero() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.findBookingsForUser(userId, "ALL", -1, 10));

        Assertions.assertEquals("Параметр запроса from: -1 не может быть меньше 0", exception.getMessage());
    }

    @Test
    void testFindBookingsThrowsBadRequestExceptionWhenSizeFromLessOrEqualsZero() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.findBookingsForUser(userId, "ALL", 0, 0));

        Assertions.assertEquals("Параметр запроса size: 0 должен быть больше 0", exception.getMessage());
    }

    @Test
    void testFindBookingsThrowsNotFoundExceptionWhenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBookingsForUser(userId, "ALL", 0, 10));

        Assertions.assertEquals("Пользователя с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testFindBookingThrowsNotFoundExceptionWhenBookingNotExists() {
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(userId, bookingId));

        Assertions.assertEquals("Бронирование с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testFindBookingThrowsNotFoundExceptionWhenUserIsNotOwnerOrBooker() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final Booking booking = mock(Booking.class);

        when(booking.getItem()).thenReturn(item);
        when(booking.getBooker()).thenReturn(user);
        when(item.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId + 1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(userId, bookingId));

        Assertions.assertEquals("Пользователь с ID: 1 не является владельцем или арендатором", exception.getMessage());
    }

    @Test
    void testCreateBookingThrowsNotFoundExceptionWhenUserNotExists() {
        final BookingCreationDto bookingCreationDto = mock(BookingCreationDto.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(userId, bookingCreationDto));

        Assertions.assertEquals("Пользователь с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testCreateBookingThrowsNotFoundExceptionWhenItemNotExists() {
        final User user = mock(User.class);
        final BookingCreationDto bookingCreationDto = mock(BookingCreationDto.class);

        when(bookingCreationDto.getItemId()).thenReturn(itemId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(userId, bookingCreationDto));

        Assertions.assertEquals("Вещь с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testCreateBookingThrowsBadRequestExceptionWhenStarsTimeIsAfterEndTime() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final BookingCreationDto bookingCreationDto = mock(BookingCreationDto.class);

        when(bookingCreationDto.getItemId()).thenReturn(itemId);
        when(bookingCreationDto.getStart()).thenReturn(LocalDateTime.now().plusDays(2));
        when(bookingCreationDto.getEnd()).thenReturn(LocalDateTime.now().plusDays(1));
        when(item.getAvailable()).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.createBooking(userId, bookingCreationDto));

        Assertions.assertEquals("Время начала бронирования не может быть позже или равно времени окончания",
                exception.getMessage());
    }

    @Test
    void testCreateBookingThrowsBadRequestExceptionWhenItemIsNotAvailable() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final BookingCreationDto bookingCreationDto = mock(BookingCreationDto.class);

        when(bookingCreationDto.getItemId()).thenReturn(itemId);
        when(bookingCreationDto.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(bookingCreationDto.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(item.getAvailable()).thenReturn(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.createBooking(userId, bookingCreationDto));

        Assertions.assertEquals("Вещь не доступна для бронирования", exception.getMessage());
    }

    @Test
    void testCreateBookingThrowsNotFoundExceptionWhenUserIsOwner() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final BookingCreationDto bookingCreationDto = mock(BookingCreationDto.class);

        when(bookingCreationDto.getItemId()).thenReturn(itemId);
        when(bookingCreationDto.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(bookingCreationDto.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(user.getId()).thenReturn(userId);
        when(item.getAvailable()).thenReturn(true);
        when(item.getOwner()).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(userId, bookingCreationDto));

        Assertions.assertEquals("Владелец вещи не может создавать запрос бронирования", exception.getMessage());
    }

    @Test
    void testCreateBookingThrowsBadRequestExceptionWhenIntersectionsExist() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final BookingCreationDto bookingCreationDto = mock(BookingCreationDto.class);
        final Booking booking = mock(Booking.class);

        when(bookingCreationDto.getItemId()).thenReturn(itemId);
        when(bookingCreationDto.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(bookingCreationDto.getEnd()).thenReturn(LocalDateTime.now().plusDays(3));
        when(booking.getStart()).thenReturn(LocalDateTime.now().plusDays(2));
        when(booking.getEnd()).thenReturn(LocalDateTime.now().plusDays(4));
        when(user.getId()).thenReturn(userId + 1);
        when(item.getAvailable()).thenReturn(true);
        when(item.getOwner()).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsForItem(anyLong())).thenReturn(List.of(booking));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.createBooking(userId, bookingCreationDto));

        Assertions.assertEquals("В указанном временном периоде уже имеется бронирование", exception.getMessage());
    }

    @Test
    void testUpdateBookingThrowsNotFoundExceptionWhenUserIsNotExists() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(userId, bookingId, true));

        Assertions.assertEquals("Пользователя с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testUpdateBookingThrowsNotFoundExceptionWhenBookingIsNotExists() {
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(userId, bookingId, true));

        Assertions.assertEquals("Booking с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testUpdateBookingThrowsNotFoundExceptionWhenUserIsNotOwner() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final Booking booking = mock(Booking.class);

        when(user.getId()).thenReturn(userId + 1);
        when(item.getId()).thenReturn(itemId);
        when(item.getOwner()).thenReturn(user);
        when(booking.getItem()).thenReturn(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(userId, bookingId, true));

        Assertions.assertEquals("Пользователь с переданным ID: 1 не является владельцем вещи с ID: 1",
                exception.getMessage());
    }

    @Test
    void testUpdateBookingThrowsBadRequestExceptionWhenBookingIsAlreadyApproved() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final Booking booking = mock(Booking.class);

        when(user.getId()).thenReturn(userId);
        when(item.getId()).thenReturn(itemId);
        when(item.getOwner()).thenReturn(user);
        when(booking.getItem()).thenReturn(item);
        when(booking.getStatus()).thenReturn(BookingStatus.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.updateBooking(userId, bookingId, true));

        Assertions.assertEquals("Владелец с ID: 1 уже одобрил бронирование вещи с ID: 1",
                exception.getMessage());
    }

    @Test
    void testDeleteBookingThrowsNotFoundExceptionWhenUserIsNotExists() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.deleteBooking(userId, bookingId));

        Assertions.assertEquals("Пользователя с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testDeleteBookingThrowsNotFoundExceptionWhenBookingIsNotExists() {
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.deleteBooking(userId, bookingId));

        Assertions.assertEquals("Booking с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testDeleteBookingThrowsNotFoundExceptionWhenUserIsNotOwnerOrBooker() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final Booking booking = mock(Booking.class);

        when(booking.getItem()).thenReturn(item);
        when(booking.getBooker()).thenReturn(user);
        when(item.getId()).thenReturn(itemId);
        when(item.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId + 1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.deleteBooking(userId, bookingId));

        Assertions.assertEquals("Пользователь с переданным ID: 1 не является ни владельцем вещи с ID: 1 " +
                "ни создателем запроса бронирования", exception.getMessage());
    }

    @Test
    void testDeleteBookingThrowsBadRequestExceptionWhenItemDidNotDelete() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final Booking booking = mock(Booking.class);

        when(user.getId()).thenReturn(userId);
        when(item.getOwner()).thenReturn(user);
        when(booking.getItem()).thenReturn(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.deleteBooking(userId, bookingId));

        Assertions.assertEquals("Запрос бронирония с id: 1 удалить не удалось", exception.getMessage());
    }
}