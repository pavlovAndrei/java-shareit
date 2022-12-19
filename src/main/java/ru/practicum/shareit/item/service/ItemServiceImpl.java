package ru.practicum.shareit.item.service;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> findAllByUserId(long userId) {
        throwIfUserDoesntExist(userId);

        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public ItemDto getById(long itemId, long userId) {
        throwIfUserDoesntExist(userId);

        var item = getItemById(itemId);

        log.info("Item '{}' is successfully retrieved", item.getName());
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        throwIfUserDoesntExist(userId);

        var item = itemRepository.add(itemMapper.toItem(itemDto), userId);
        log.info("Item '{}' is successfully added", item.getName());
        return itemMapper.toItemDto(item);
    }

    @Override
    public @Valid ItemDto update(ItemDto itemDto, long itemId, long userId) {
        var item = getItemById(itemId);

        if (item.getOwner() != userId) {
            throw new NotFoundException(format("User with id '%d' doesn't have an item '%s'", userId, item.getName()));
        }

        if (isNoneBlank(itemDto.getName())) {
            item.setName(itemDto.getName());
        }

        if (isNoneBlank(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }

        if (nonNull(itemDto.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(long userId, String searchText) {
        if (searchText.isBlank()) {
            log.warn("The empty search text was received for searching");
            return Collections.emptyList();
        }

        throwIfUserDoesntExist(userId);

        return itemRepository.search(searchText)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    private Item getItemById(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(format("Item with id '%d' does not exist", id)));
    }

    private void throwIfUserDoesntExist(long id) {
        if (!userRepository.exists(id)) {
            throw new NotFoundException(format("User with id '%d' does not exist", id));
        }
    }
}
