package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentDto {

    @Positive
    private Long id;

    @NotBlank
    private String text;

    private Long itemId;

    private String authorName;

    @Past
    private LocalDateTime created;
}
