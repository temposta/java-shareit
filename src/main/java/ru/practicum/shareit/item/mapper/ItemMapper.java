package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;

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
                .available(itemCreateDto.getAvailable())
                .build();
    }
}
