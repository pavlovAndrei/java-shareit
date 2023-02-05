package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

@Data
@Builder
public class ItemRequestDto {

    @Positive
    @NotNull
    private Long id;

    @NotBlank
    private String description;

    @Positive
    @NotNull
    private Long requestorId;

    @Past
    private LocalDateTime created;

    private List<ItemDto> items;
}
