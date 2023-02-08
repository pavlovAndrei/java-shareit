package ru.practicum.shareit.request.service;

import java.util.List;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;

public interface ItemRequestService {

    List<ItemRequestDto> findAllByOwnerId(long userId);

    List<ItemRequestDto> getAll(long userId, Integer offset, Integer size);

    ItemRequestDto getById(long userId, long requestId);

    ItemRequestDto add(long userId, ItemRequestPostDto itemRequestPostDto);
}
