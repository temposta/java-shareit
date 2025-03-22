package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Основной объект для пользователя.
 */
@Data
public class User {
    private Long id;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String name;
    @NotBlank(message = "Электронная почта должна быть указана")
    @Email(message = "Адрес электронной почты не соответствует формату")
    private String email;
}
