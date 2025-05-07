package ru.practicum.shareit.item.dto;

import lombok.Data;

/**
 * DTO-объект для обновления данных вещи.
 */
@Data
public class ItemPatchDto {
    private String name;
    private String description;
    private Boolean available;
}
