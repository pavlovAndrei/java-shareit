package ru.practicum.shareit.request.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.user.repository.UserRepository;

@Validated
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;


    @Override
    public List<ItemRequestDto> findAllByOwnerId(long userId) {
        checkUserExists(userId);

        var itemRequests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId);

        return addItemsToRequests(convertItemRequestsToItemRequestDtoList(itemRequests));
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, Integer offset, Integer size) {
        checkUserExists(userId);

        Pageable pageable = CustomPageRequest.of(offset, size);

        var itemRequestList = itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(userId, pageable).getContent();

        return addItemsToRequests(convertItemRequestsToItemRequestDtoList(itemRequestList));
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        checkUserExists(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Item request with id: '%d' does not exist",
                        requestId)));

        List<Long> requestsId = List.of(itemRequest.getId());

        List<Item> items = itemRepository.findAllByRequestIdIn(requestsId);

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(convertItemsToItemDtoList(items));

        return itemRequestDto;
    }

    @Transactional
    @Override
    public @Valid ItemRequestDto add(long userId, ItemRequestPostDto itemRequestPostDto) {
        checkUserExists(userId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestPostDto);
        itemRequest.setRequestorId(userId);
        itemRequest.setCreated(now());

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    private List<ItemRequestDto> addItemsToRequests(List<ItemRequestDto> result) {
        List<Long> requestsId = result.stream()
                .map(ItemRequestDto::getId)
                .collect(toList());

        List<Item> items = itemRepository.findAllByRequestIdIn(requestsId);

        Map<Long, List<ItemDto>> map = new HashMap<>();

        for (ItemDto itemDto : convertItemsToItemDtoList(items)) {
            List<ItemDto> list = map.getOrDefault(itemDto.getRequestId(), new ArrayList<>());
            list.add(itemDto);
            map.put(itemDto.getRequestId(), list);
        }

        for (ItemRequestDto itemRequestDto : result) {
            itemRequestDto.setItems(map.getOrDefault(itemRequestDto.getId(), emptyList()));
        }

        return result;
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(format("User with id '%d' does not exist", userId));
        }
    }

    private List<ItemRequestDto> convertItemRequestsToItemRequestDtoList(List<ItemRequest> items) {
        return items.stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(toList());
    }

    private List<ItemDto> convertItemsToItemDtoList(List<Item> items) {
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }
}
