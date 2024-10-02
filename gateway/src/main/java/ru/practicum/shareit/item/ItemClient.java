package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;

import java.util.ArrayList;
import java.util.Map;

import static ru.practicum.shareit.constants.Constants.SERVER_URL;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value(SERVER_URL) String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addNewItem(Long userId, InputItemDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, InputItemDto dto) {
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findByText(Long userId, String text, Integer from, Integer size) {
        if (text.isBlank() || text.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        }

        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto dto) {
        return post("/" + itemId + "/comment", userId, dto);
    }
}
