package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

@Data
@Builder
public class ItemRequestDto {

    private Long id;

    private String description;

    private Long requestorId;

    private LocalDateTime created;

    private List<ItemDto> items;
}
