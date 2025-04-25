package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Преобразователь (mapper) для DTO Item.
 */
@Component
public class ItemMapper {

    /**
     * Метод для преобразования из ItemCreateDto в Item.
     *
     * @param itemCreateDto исходные данные для преобразования.
     * @return результат преобразования в Item.
     */
    public Item fromCreateDto(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .isAvailable(itemCreateDto.getAvailable())
                .build();
    }

    /**
     * Метод для преобразования из Item в ItemDto.
     *
     * @param item исходный объект для преобразования.
     * @return результат преобразования в ItemDto.
     */
    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner() == null ? null : item.getOwner())
                .available(item.getIsAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    /**
     * Метод для преобразования списка Item в список ItemDto.
     *
     * @param items исходный список объектов для преобразования.
     * @return результирующий список объектов ItemDto.
     */
    public List<ItemDto> toDto(List<Item> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(this::toDto)
                .toList();
    }
}
