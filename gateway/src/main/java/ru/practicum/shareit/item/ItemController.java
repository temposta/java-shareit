package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemPatchRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating new item {} by owner with id {}", itemRequestDto, ownerId);
        return itemClient.createItem(ownerId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                            @PathVariable long itemId,
                                            @RequestBody @Valid ItemPatchRequestDto itemPatchRequestDto) {
        log.info("Patching item id {} by owner with id {} on data {}", itemId, ownerId, itemPatchRequestDto);
        return itemClient.patchItem(ownerId, itemId, itemPatchRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {
        log.info("Getting item with id {} by user with id {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Getting items by owner with id {}", ownerId);
        return itemClient.getItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsWithText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam("text") String text) {
        log.info("Getting items by text {} from user with id {}", text, userId);
        return itemClient.getItemsWithText(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @PathVariable long itemId) {
        log.info("Deleting item with id {} from user with id {}", itemId, ownerId);
        return itemClient.deleteItem(ownerId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("Posting comment with id {} to item with id {}, text {}", bookerId, itemId, commentRequestDto.getText());
        return itemClient.addComment(bookerId, itemId, commentRequestDto);
    }
}
