package ru.practicum.shareit.request;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.empty;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemMapper itemMapper;

    @Test
    void add() {
        long userId = 1L;
        var itemRequestPostDto = new ItemRequestPostDto();
        var itemRequest = ItemRequest.builder().build();
        var expectedItemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .created(itemRequest.getCreated())
                .requestorId(itemRequest.getRequestorId())
                .build();

        when(itemRequestMapper.toItemRequest(itemRequestPostDto))
                .thenReturn(itemRequest);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(itemRequest))
                .thenReturn(expectedItemRequestDto);

        var actualItemRequestDto = itemRequestService.add(userId, itemRequestPostDto);

        assertThat(actualItemRequestDto)
                .isEqualTo(expectedItemRequestDto);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void getAllByOwner() {
        long userId = 1L;
        List<Item> itemList = List.of();

        var itemRequestDto = ItemRequestDto.builder().id(1L).build();
        var itemRequestList = List.of(ItemRequest.builder().id(1L).build());
        var expectedItemRequestDtoList = List.of(ItemRequestDto.builder().id(1L).items(List.of()).build());

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(itemRequestList);
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(itemList);
        when(itemRequestMapper.toItemRequestDto(any()))
                .thenReturn(itemRequestDto);

        var actualItemRequestDtoList = itemRequestService.findAllByOwnerId(userId);

        assertThat(actualItemRequestDtoList)
                .isEqualTo(expectedItemRequestDtoList);
    }

    @Test
    void getAll() {
        long userId = 1L;
        int from = 1;
        int size = 2;
        Pageable pageable = CustomPageRequest.of(from, size);
        Page<ItemRequest> page = Page.empty();
        var itemRequestDto = ItemRequestDto.builder().id(1L).build();

        ItemDto itemDto = ItemDto.builder().id(1L).requestId(1L).build();
        List<Item> items = List.of(new Item());

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(userId, pageable))
                .thenReturn(page);
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(items);
        when(itemMapper.toItemDto(any()))
                .thenReturn(itemDto);

        itemRequestService.getAll(userId, from, size);

        assertThat(List.of(itemRequestDto)).isNotEmpty();
    }

    @Test
    void getById() {
        long userId = 1L;
        long requestId = 1L;
        var itemRequest = ItemRequest.builder().id(requestId).build();
        var expectedItemRequestDto = ItemRequestDto.builder().build();

        List<Item> items = List.of();

        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(items);
        when(itemRequestMapper.toItemRequestDto(itemRequest))
                .thenReturn(expectedItemRequestDto);

        var actualItemRequestDto = itemRequestService.getById(userId, requestId);

        assertThat(actualItemRequestDto)
                .isEqualTo(expectedItemRequestDto);
    }

    @Test
    void getById_whenItemRequestNotFound_throwNotFoundException() {
        long userId = 1L;
        long requestId = 1L;

        when(itemRequestRepository.findById(requestId))
                .thenReturn(empty());
        when(userRepository.existsById(userId))
                .thenReturn(true);

        var exception = assertThrows(NotFoundException.class, () ->
                itemRequestService.getById(userId, requestId));

        assertThat(exception.getMessage())
                .isEqualTo(format("Item request with id: '%d' does not exist", requestId));
    }
}
