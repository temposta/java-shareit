package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemOwnerView;

import java.util.List;

public interface ItemService {
    Item addNewItem(long ownerId, ItemCreateDto itemCreateDto);

    List<ItemOwnerView> getItemsForOwner(long ownerId);

    void deleteItem(long ownerId, long itemId);

    Item patchItem(long ownerId, long itemId, ItemPatchDto itemPatchDto);

    Item getItem(long itemId);

    List<Item> getItemsWithText(String text);

    Comment addComment(long bookerId, long itemId, CommentCreateDto commentCreateDto);

    ItemOwnerView getItemForOwnerView(long itemId);
}
