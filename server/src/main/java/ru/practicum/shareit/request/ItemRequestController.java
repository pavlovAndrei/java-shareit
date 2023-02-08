package ru.practicum.shareit.request;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/requests")
public class ItemRequestController {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                  @PathVariable long requestId) {
        return itemRequestService.getById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByOwner(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return itemRequestService.findAllByOwnerId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getAll(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto add(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                              @RequestBody ItemRequestPostDto itemRequestPostDto) {
        return itemRequestService.add(userId, itemRequestPostDto);
    }
}
