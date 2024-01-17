package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findBookingsForUser(long userId, QueryState state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findBookingsForOwner(long userId, QueryState state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findBooking(long userId, long bookingId) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId
        );
        return get("/{bookingId}", userId, parameters);
    }

    public ResponseEntity<Object> createBooking(long userId, BookingCreationDto bookingCreationDto) {
        return post("", userId, bookingCreationDto);
    }

    public ResponseEntity<Object> updateBooking(long ownerId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId,
                "approved", approved
        );
        return patch("/{bookingId}?approved={approved}", ownerId, parameters, null);
    }

    public ResponseEntity<Object> deleteBooking(long ownerId, long bookingId) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId
        );
        return delete("/{bookingId}", ownerId, parameters);
    }
}