package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findBookingsForUser(long userId, String state, int from, int size) {
        log.info("Запрос списка бронирования вещей пользователем с ID: " + userId);

        PageRequest page = getPage(from, size);

        QueryState queryState = Enum.valueOf(QueryState.class, state);

        checkUserExists(userId);

        List<Booking> bookings = new ArrayList<>();

        switch (queryState) {
            case ALL:
                bookings = bookingRepository.findBookingsForUser(userId, page);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusForUser(userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusForUser(userId, BookingStatus.REJECTED, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsForUser(userId, page)
                        .stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsForUser(userId, page)
                        .stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now())
                                && b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findBookingsForUser(userId, page)
                        .stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
        }

        return BookingMapper.INSTANCE.mapToBookingDto(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findBookingsForOwner(long ownerId, String state, int from, int size) {
        log.info("Запрос списка бронирования вещей владельца с ID: " + ownerId);

        PageRequest page = getPage(from, size);

        QueryState queryState = Enum.valueOf(QueryState.class, state);

        checkUserExists(ownerId);

        List<Booking> bookings = new ArrayList<>();

        switch (queryState) {
            case ALL:
                bookings = bookingRepository.findBookingsForOwner(ownerId, page);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusForOwner(ownerId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusForOwner(ownerId, BookingStatus.REJECTED, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsForOwner(ownerId, page)
                        .stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsForOwner(ownerId, page)
                        .stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now())
                                && b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findBookingsForOwner(ownerId, page)
                        .stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
        }

        return BookingMapper.INSTANCE.mapToBookingDto(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findBooking(long userId, long bookingId) {
        log.info("Запрос бронирования с ID: " + bookingId);

        checkUserExists(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID: " + bookingId + " не существует"));

        if (userId == booking.getItem().getOwner().getId() || userId == booking.getBooker().getId()) {
            return BookingMapper.INSTANCE.mapToBookingDto(booking);
        } else {
            String message = "Пользователь с ID: " + userId + " не является владельцем или арендатором";

            log.info(message);

            throw new NotFoundException(message);
        }
    }

    @Override
    public BookingDto createBooking(long userId, BookingCreationDto bookingCreationDto) {
        log.info("Запрос бронирования вещи с id: " + bookingCreationDto.getItemId()
                + " от пользователя с id: " + userId);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не существует"));
        Item item = itemRepository.findById(bookingCreationDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + bookingCreationDto.getItemId()
                        + " не существует"));

        String message = null;

        if (!bookingCreationDto.getStart().isBefore(bookingCreationDto.getEnd())) {
            message = "Время начала бронирования не может быть позже или равно времени окончания";
        }

        if (!item.getAvailable()) {
            message = "Вещь не доступна для бронирования";
        }

        if (message != null) {
            log.info(message);

            throw new BadRequestException(message);
        }

        if (item.getOwner().getId() == userId) {
            message = "Владелец вещи не может создавать запрос бронирования";

            log.info(message);

            throw new NotFoundException(message);
        }

        bookingCreationDto.setStatus(BookingStatus.WAITING);

        checkingForNonIntersections(bookingCreationDto);

        Booking booking = BookingMapper.INSTANCE.mapToNewBooking(bookingCreationDto, booker, item);

        return BookingMapper.INSTANCE.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(long ownerId, long bookingId, boolean approved) {
        log.info("Запрос подтверждения бронирования с id: " + bookingId + " владельцем с id: " + ownerId);

        checkUserExists(ownerId);

        Booking updatedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking с ID: " + bookingId + " не существует"));

        if (ownerId != updatedBooking.getItem().getOwner().getId()) {
            String message = "Пользователь с переданным ID: " + ownerId + " не является владельцем вещи с ID: "
                    + updatedBooking.getItem().getId();

            log.info(message);

            throw new NotFoundException(message);
        }

        if (updatedBooking.getStatus() == BookingStatus.APPROVED) {
            String message = "Владелец с ID: " + ownerId + " уже одобрил бронирование вещи с ID: "
                    + updatedBooking.getItem().getId();

            log.info(message);

            throw new BadRequestException(message);
        }

        if (approved) {
            updatedBooking.setStatus(BookingStatus.APPROVED);
        } else {
            updatedBooking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.INSTANCE.mapToBookingDto(bookingRepository.save(updatedBooking));
    }

    @Override
    public ResponseFormat deleteBooking(long userId, long bookingId) {
        log.info("Запрос удаления бронирования с id: " + bookingId + " пользователем с id: " + userId);

        checkUserExists(userId);

        Booking removableBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking с ID: " + bookingId + " не существует"));

        if ((userId != removableBooking.getItem().getOwner().getId())
                && (userId != removableBooking.getBooker().getId())) {
            String message = "Пользователь с переданным ID: " + userId + " не является ни владельцем вещи с ID: "
                    + removableBooking.getItem().getId() + " ни создателем запроса бронирования";

            log.info(message);

            throw new NotFoundException(message);
        }

        bookingRepository.deleteById(bookingId);

        if (bookingRepository.findById(bookingId).isEmpty()) {
            String message = "Запрос бронирования с id: " + bookingId + " успешно удален";

            log.info(message);

            return new ResponseFormat(message, HttpStatus.OK);
        } else {
            String message = "Запрос бронирония с id: " + bookingId + " удалить не удалось";

            log.warn(message);

            throw new BadRequestException(message);
        }
    }

    private void checkingForNonIntersections(BookingCreationDto bookingCreationDto) {
        List<Booking> bookingsForItem = bookingRepository.findBookingsForItem(bookingCreationDto.getItemId());

        if (bookingsForItem.isEmpty()) {
            return;
        }

        for (Booking booking : bookingsForItem) {
            LocalDateTime startTime = bookingCreationDto.getStart();
            LocalDateTime endTime = bookingCreationDto.getEnd();

            if (startTime.isBefore(booking.getEnd()) && endTime.isAfter(booking.getStart())) {
                String message = "В указанном временном периоде уже имеется бронирование";

                log.info(message);

                throw new BadRequestException(message);
            }
        }
    }

    private void checkUserExists(long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            String message = "Пользователя с ID: " + userId + " не существует";

            log.info(message);

            throw new NotFoundException(message);
        }
    }

    private PageRequest getPage(int from, int size) {
        if (from < 0) {
            String message = "Параметр запроса from: " + from + " не может быть меньше 0";

            log.info(message);

            throw new BadRequestException(message);
        }

        if (size <= 0) {
            String message = "Параметр запроса size: " + size + " должен быть больше 0";

            log.info(message);

            throw new BadRequestException(message);
        }

        return PageRequest.of(from > 0 ? from / size : 0, size);
    }
}