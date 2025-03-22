package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.ForbiddenException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

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

    /**
     * Метод для создания новой вещи
     *
     * @param ownerId       ID владельца вещи
     * @param itemCreateDto данные для создания вещи
     * @return созданная вещь
     */
    @Override
    public Item addNewItem(long ownerId, ItemCreateDto itemCreateDto) {
        checkUser(ownerId);
        Item item = itemMapper.fromCreateDto(itemCreateDto);
        item.setOwner(ownerId);
        return itemRepository.create(item);
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
        if (ownerId != patchItem.getOwner()) {
            throw new ForbiddenException(String
                    .format("Пользователь с id: %s не является владельцем вещи", ownerId));
        }
        if (itemPatchDto.getAvailable() != null) {
            patchItem.setAvailable(itemPatchDto.getAvailable());
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
     * Метод для получения владельцем списка всех его вещей с указанием названия и описания для каждой из них.
     *
     * @param ownerId ID владельца вещей.
     * @return список всех вещей владельца с указанным ID.
     */
    @Override
    public List<Item> getItems(long ownerId) {
        checkUser(ownerId);
        return itemRepository.findItemsByOwnerId(ownerId);
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
        return itemRepository.findAllWithTextFilter(text);
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
        if (ownerId != deletedItem.getOwner()) {
            throw new ForbiddenException(String
                    .format("Пользователь с id: %s не является владельцем вещи", ownerId));
        }
        itemRepository.deleteById(itemId);
    }

    private void checkUser(long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %s не найден", userId)));
    }
}
