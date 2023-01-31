package ru.practicum.shareit.item;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.service.ItemService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemGetDto> findAllByUserId(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer offset,
                                            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return itemService.findAllByUserId(userId, offset, size);
    }

    @GetMapping("/{itemId}")
    public ItemGetDto getById(@PathVariable long itemId,
                              @RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                @RequestParam String text,
                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer offset,
                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        return itemService.search(userId, text, offset, size);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                       @RequestBody @Valid ItemDto itemDto) {
        return itemService.add(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                 @PathVariable @Positive long itemId,
                                 @RequestBody @Valid CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(itemDto, itemId, userId);
    }
}
