package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO-объект для создания запроса.
 */
@Data
public class BookingCreateDto {
    private Long itemId;
    @Future
    @NotNull
    private LocalDateTime start;
    @Future
    @NotNull
    private LocalDateTime end;
}