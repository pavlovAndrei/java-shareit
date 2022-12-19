package ru.practicum.shareit.item.service;

import java.util.List;

import ru.practicum.shareit.item.ItemDto;

public interface ItemService {

    List<ItemDto> findAllByUserId(long userId);

    ItemDto getById(long itemId, long userId);

    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long itemId, long userId);

    List<ItemDto> search(long userId, String searchText);
}
