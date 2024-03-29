package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findUsersItemRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findItemRequests(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findItemRequest(Long userId, long requestId) {
        Map<String, Object> parameters = Map.of(
                "requestId", requestId
        );
        return get("/{requestId}", userId, parameters);
    }

    public ResponseEntity<Object> createItemRequest(Long userId, ItemRequestCreationDto itemRequest) {
        return post("", userId, itemRequest);
    }

    public ResponseEntity<Object> deleteItemRequest(long userId, long requestId) {
        Map<String, Object> parameters = Map.of(
                "requestId", requestId
        );
        return delete("/{requestId}", userId, parameters);
    }
}