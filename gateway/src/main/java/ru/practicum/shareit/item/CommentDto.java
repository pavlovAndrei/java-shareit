package ru.practicum.shareit.item;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@ToString
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
