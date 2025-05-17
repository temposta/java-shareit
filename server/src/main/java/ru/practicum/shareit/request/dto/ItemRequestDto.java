package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.ItemShort;

import java.sql.Timestamp;
import java.util.List;

/**
 * DTO объект для запросов вещи.
 */
@Data
public class ItemRequestDto {
    private Long id;

    private String description;

    private Timestamp created;

    private List<ItemShort> items;
}
