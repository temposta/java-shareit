package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserPatchDto {
    private String name;
    @Email(message = "Адрес электронной почты не соответствует формату")
    private String email;
}
