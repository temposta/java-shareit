package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.ItemShort;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestWithItems;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequest toItemRequest(Long requestorId, ItemRequestCreateDto createDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestorId(requestorId);
        itemRequest.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        itemRequest.setDescription(createDto.getDescription());
        return itemRequest;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreatedDate());
        return itemRequestDto;
    }

    public ItemRequestDto toItemRequestDto(ItemRequestWithItems itemRequestWithItems) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequestWithItems.getId());
        itemRequestDto.setDescription(itemRequestWithItems.getDescription());
        itemRequestDto.setCreated(itemRequestWithItems.getCreatedDate());
        itemRequestDto.setItems(itemRequestWithItems.getItems()
                .stream()
                .map(itemSimple -> new ItemShort(itemSimple.getId(),
                        itemSimple.getName(),
                        itemSimple.getOwnerId()))
                .toList());
        return itemRequestDto;
    }

    public List<ItemRequestDto> toItemRequestDto(List<ItemRequestWithItems> itemRequestWithItems) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequestWithItems itemRequestWithItem : itemRequestWithItems) {
            itemRequestDtos.add(toItemRequestDto(itemRequestWithItem));
        }
        return itemRequestDtos;
    }
}
