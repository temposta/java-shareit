package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item create(Item item);

    Optional<Item> findById(Long id);

    List<Item> findAllWithTextFilter(String text);

    List<Item> findItemsByOwnerId(long ownerId);

    Item save(Item patchItem);

    void deleteById(long itemId);
}
