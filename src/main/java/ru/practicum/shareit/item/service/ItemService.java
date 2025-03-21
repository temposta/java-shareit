package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addNewItem(long ownerId, ItemCreateDto itemCreateDto);

    List<Item> getItems(long ownerId);

    void deleteItem(long ownerId, long itemId);

    Item patchItem(long ownerId, long itemId, ItemPatchDto itemPatchDto);

    Item getItem(long itemId);

    List<Item> getItemsWithText(String text);
}
