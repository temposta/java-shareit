package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.StatusEnum;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.ForbiddenException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ResourceUnavailableException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemOwnerView;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemOwnerViewRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация бизнес-логики для работы с вещами.
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemOwnerViewRepository itemOwnerViewRepository;

    /**
     * Метод для создания новой вещи
     *
     * @param ownerId       ID владельца вещи
     * @param itemCreateDto данные для создания вещи
     * @return созданная вещь
     */
    @Override
    public Item addNewItem(long ownerId, ItemCreateDto itemCreateDto) {
        User owner = checkUser(ownerId);
        Item item = itemMapper.fromCreateDto(itemCreateDto);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    /**
     * Метод для редактирования вещи. Изменить можно название, описание и статус доступа к аренде.
     * Редактировать вещь может только её владелец.
     *
     * @param ownerId      ID пользователя, который направил запрос на редактирования.
     * @param itemId       ID вещи для редактирования.
     * @param itemPatchDto данные, которые подлежат редактированию.
     */
    @Override
    public Item patchItem(long ownerId, long itemId, ItemPatchDto itemPatchDto) {
        checkUser(ownerId);
        Item patchItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("Вещь с указанным id: %s не найдена", itemId)));
        if (ownerId != patchItem.getOwner().getId()) {
            throw new ForbiddenException(String
                    .format("Пользователь с id: %s не является владельцем вещи", ownerId));
        }
        if (itemPatchDto.getAvailable() != null) {
            patchItem.setIsAvailable(itemPatchDto.getAvailable());
        }
        if (itemPatchDto.getName() != null) {
            patchItem.setName(itemPatchDto.getName());
        }
        if (itemPatchDto.getDescription() != null) {
            patchItem.setDescription(itemPatchDto.getDescription());
        }
        return itemRepository.save(patchItem);
    }

    /**
     * Метод для получения информации о вещи по ее идентификатору.
     *
     * @param itemId ID вещи для получения.
     * @return полученная информация о запрошенной вещи.
     */
    @Override
    public Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("Вещь с указанным id: %s не найдена", itemId)));
    }

    /**
     * Метод для получения информации о вещи по ее идентификатору,
     * включая информацию о предыдущем и следующем бронированиях,
     * а также о добавленных комментариях.
     *
     * @param itemId ID вещи для получения.
     * @return полученная информация о запрошенной вещи.
     */
    @Override
    public ItemOwnerView getItemForOwnerView(long itemId) {
        return itemOwnerViewRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String
                .format("Вещь с id - %s не найдена", itemId)));
    }

    /**
     * Метод для получения владельцем списка всех его вещей с указанием названия и описания для каждой из них.
     *
     * @param ownerId ID владельца вещей.
     * @return список всех вещей владельца с указанным ID.
     */
    @Override
    public List<ItemOwnerView> getItemsForOwner(long ownerId) {
        checkUser(ownerId);
        return itemOwnerViewRepository.findAllByOwnerId(ownerId);
    }

    /**
     * Метод для поиска вещей по тексту, содержащемуся в названии или описании.
     * Поиск возвращает только доступные для аренды вещи.
     *
     * @param text текст для поиска вещей.
     * @return список найденных вещей согласно условиям запроса.
     */
    @Override
    public List<Item> getItemsWithText(String text) {
        return itemRepository.findByIsAvailableIsTrueAndNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(text, text);
    }

    /**
     * Метод для добавления комментариев к ранее бронированной вещи.
     *
     * @param bookerId         ID пользователя, который добавляет комментарий.
     * @param itemId           ID вещи, к которой относится комментарий.
     * @param commentCreateDto тело запроса, содержащее комментарий в виде текста.
     * @return созданный комментарий.
     */
    @Override
    public Comment addComment(long bookerId, long itemId, CommentCreateDto commentCreateDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещи с ID - %s - не существует", itemId)));
        bookingRepository.findByItemIdAndBookerIdAndStartDateBeforeAndStatus(itemId,
                        bookerId, LocalDateTime.now(), StatusEnum.APPROVED)
                .orElseThrow(() -> new ResourceUnavailableException("Добавление комментария невозможно."));
        User booker = checkUser(bookerId);
        Comment comment = new Comment();
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setText(commentCreateDto.getText());
        comment.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return commentRepository.save(comment);
    }

    /**
     * Удаление вещи владельцем.
     *
     * @param ownerId ID пользователя, направившего запрос на удаление.
     * @param itemId  ID вещи для удаления.
     */
    @Override
    public void deleteItem(long ownerId, long itemId) {
        checkUser(ownerId);
        Item deletedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("Вещь с указанным id: %s не найдена", itemId)));
        if (ownerId != deletedItem.getOwner().getId()) {
            throw new ForbiddenException(String
                    .format("Пользователь с id: %s не является владельцем вещи", ownerId));
        }
        itemRepository.deleteById(itemId);
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %s не найден", userId)));
    }
}
