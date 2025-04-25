package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class CommentDto {

    private Long id;

    private String text;

    private Long itemId;

    private String authorName;

    private Timestamp created;
}
