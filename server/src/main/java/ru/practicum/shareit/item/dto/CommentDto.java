package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@ToString
@Data
public class CommentDto {

    private Long id;

    private String text;

    private Long itemId;

    private String authorName;

    private LocalDateTime created;
}
