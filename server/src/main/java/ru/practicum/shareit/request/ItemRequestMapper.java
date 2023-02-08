package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;

@Component
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestPostDto itemRequestPostDto) {
        return ItemRequest.builder()
                .description(itemRequestPostDto.getDescription())
                .build();
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestorId())
                .created(itemRequest.getCreated())
                .build();
    }
}
