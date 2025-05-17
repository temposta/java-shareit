package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    @NotBlank(message = "Поле наименование должно быть заполнено")
    private String name;
    @NotBlank(message = "Поле описания должно быть заполнено")
    private String description;
    @NotNull(message = "Доступность вещи должна быть определена")
    private Boolean available;
    private Long requestId;
}
