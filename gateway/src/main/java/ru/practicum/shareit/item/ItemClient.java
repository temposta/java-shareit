package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemPatchRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long ownerId, ItemRequestDto itemRequestDto) {
        return post("", ownerId, itemRequestDto);
    }

    public ResponseEntity<Object> patchItem(long ownerId, long itemId, ItemPatchRequestDto itemPatchRequestDto) {
        return patch("/" + itemId, ownerId, itemPatchRequestDto);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> getItemsWithText(long userId, String text) {
        Map<String, Object> params = Map.of("text", text);
        return get("/search?text={text}", userId, params);
    }

    public ResponseEntity<Object> deleteItem(long ownerId, long itemId) {
        return delete("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> addComment(long bookerId, long itemId, CommentRequestDto commentRequestDto) {
        return post("/" + itemId + "/comment", bookerId, commentRequestDto);
    }
}
