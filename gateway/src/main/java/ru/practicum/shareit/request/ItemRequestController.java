package ru.practicum.shareit.request;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid ItemRequestPostDto itemRequestPostDto) {
        return itemRequestClient.createRequest(itemRequestPostDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                         Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10")
                                                         Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable @Positive Long requestId) {
        return itemRequestClient.getRequestById(requestId, userId);
    }
}
