package ru.practicum.shareit.item.service;

import java.util.List;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;

public interface ItemService {

    List<ItemGetDto> findAllByUserId(long userId);

    ItemGetDto getById(long itemId, long userId);

    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long itemId, long userId);

    List<ItemDto> search(long userId, String searchText);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
