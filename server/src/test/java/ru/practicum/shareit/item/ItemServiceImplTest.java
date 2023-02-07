package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemMapper itemMapper;


    @Test
    void addItem_whenExist() {
        long userId = 1L;
        var initialItem = new ItemDto();

        when(itemMapper.toItem(any(ItemDto.class)))
                .thenReturn(new Item());

        Item item = itemMapper.toItem(initialItem);
        item.setOwnerId(userId);

        ItemDto expectedItemDto = ItemDto.builder().id(userId).build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.save(item))
                .thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class)))
                .thenReturn(expectedItemDto);

        ItemDto actualItemDto = itemService.add(initialItem, userId);

        assertThat(actualItemDto).isEqualTo(expectedItemDto);
    }

    @Test
    void addItem_whenNotExist_throwNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.add(new ItemDto(), 999L));

        verify(itemRepository, never()).save(new Item());
    }

    @Test
    void updateItem() {
        long itemId = 1L;
        long userId = 1L;

        var itemDto = ItemDto.builder()
                .name("Test item one")
                .description("Test desc one")
                .available(false)
                .build();

        var item = Item.builder()
                .id(itemId)
                .name("Test item one")
                .description("Test desc")
                .available(true)
                .ownerId(userId)
                .build();

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        var expectedItemDto = itemMapper.toItemDto(item);
        var actualItemDto = itemService.update(itemDto, itemId, userId);

        assertThat(actualItemDto).isEqualTo(expectedItemDto);
    }

    @Test
    void updateItem_whenUserIsNotOwner_throwNotFoundException() {
        long itemId = 1L;
        long ownerId = 1L;
        long userId = 2L;

        ItemDto itemDto = new ItemDto();

        Item item = Item.builder()
                .id(itemId)
                .ownerId(ownerId)
                .build();

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        var exception = assertThrows(NotFoundException.class,
                () -> itemService.update(itemDto, itemId, userId));

        assertThat(exception.getMessage())
                .isEqualTo("User with id '2' doesn't have an item 'null'");
    }

    @Test
    void searchItem() {
        var from = 0;
        var size = 3;
        var text = "search_text";
        var userId = 1L;

        List<Item> items = List.of(new Item());
        Page<Item> itemsPage = new PageImpl<>(items);

        when(itemRepository.search(text, CustomPageRequest.of(from, size)))
                .thenReturn(itemsPage);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        List<ItemDto> expectedList = itemsPage.getContent().stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
        List<ItemDto> actualList = itemService.search(userId, text, from, size);

        assertThat(expectedList).isEqualTo(actualList);
    }
}
