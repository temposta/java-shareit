package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemOwnerView;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * Контроллер для работы с Items
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    /**
     * Зависимость контроллера от ItemService
     */
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    /**
     * Метод для создания новой вещи
     *
     * @param ownerId       ID пользователя-владельца, отправившего запрос
     * @param itemCreateDto данные для создания новой вещи
     * @return созданная вещь
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @RequestBody @Valid ItemCreateDto itemCreateDto) {
        log.info("Creating new item {} by owner with id {}", itemCreateDto, ownerId);
        Item item = itemService.addNewItem(ownerId, itemCreateDto);
        return itemMapper.toDto(item);
    }

    /**
     * Метод для редактирования вещи. Изменить можно название, описание и статус доступа к аренде.
     * Редактировать вещь может только её владелец.
     *
     * @param ownerId      ID пользователя, который направил запрос на редактирования.
     * @param itemId       ID вещи для редактирования.
     * @param itemPatchDto данные, которые подлежат редактированию.
     */
    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                             @PathVariable long itemId,
                             @RequestBody @Valid ItemPatchDto itemPatchDto) {
        log.info("Patching item id {} by owner with id {} on data {}", itemId, ownerId, itemPatchDto);
        Item item = itemService.patchItem(ownerId, itemId, itemPatchDto);
        return itemMapper.toDto(item);
    }

    /**
     * Метод для просмотра информации о конкретной вещи по её идентификатору.
     * Информацию о вещи может просмотреть любой пользователь.
     *
     * @param userId ID пользователя, направившего запрос.
     * @param itemId ID вещи, которая необходима для просмотра.
     * @return информация по запрошенной вещи.
     */
    @GetMapping("/{itemId}")
    public ItemOwnerView getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId) {
        log.info("Getting item with id {} by user with id {}", itemId, userId);
        ItemOwnerView item = itemService.getItemForOwnerView(itemId);
        log.info(String.valueOf(item));
        return item;
    }

    /**
     * Метод для просмотра владельцем списка всех его вещей с указанием названия и описания для каждой из них.
     *
     * @param ownerId ID владельца вещей.
     * @return список всех вещей владельца с указанным ID.
     */
    @GetMapping
    public List<ItemOwnerView> getItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Getting items by owner with id {}", ownerId);
        List<ItemOwnerView> items = itemService.getItemsForOwner(ownerId);
        log.info("Got {} items", items.size());
        return items;
    }

    /**
     * Метод для поиска вещи потенциальным арендатором.
     * Пользователь передаёт в строке запроса текст, и система ищет вещи, содержащие этот текст в названии или описании.
     * Поиск возвращает только доступные для аренды вещи.
     *
     * @param userId ID пользователя, направившего запрос.
     * @param text   текст для поиска вещей.
     * @return список найденных вещей согласно условиям запроса.
     */
    @GetMapping("/search")
    public List<ItemDto> getItemsWithText(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam("text") String text) {
        log.info("Getting items by text {} from user with id {}", text, userId);
        List<Item> items = itemService.getItemsWithText(text);
        return itemMapper.toDto(items);
    }

    /**
     * Удаление вещи владельцем.
     *
     * @param ownerId ID пользователя, направившего запрос на удаление.
     * @param itemId  ID вещи для удаления.
     */
    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                           @PathVariable long itemId) {
        log.info("Deleting item with id {} from user with id {}", itemId, ownerId);
        itemService.deleteItem(ownerId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                 @PathVariable long itemId,
                                 @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Posting comment with id {} to item with id {}", bookerId, itemId);
        Comment comment = itemService.addComment(bookerId, itemId, commentCreateDto);
        log.info("Comment {} added to item with id {}", comment, itemId);
        return commentMapper.toDto(comment);
    }
}
