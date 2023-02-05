package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.service.BookingService;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                          @RequestBody @Valid BookingPostDto bookingPostDto) {
        return bookingService.add(userId, bookingPostDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable @Positive long bookingId,
                              @RequestParam boolean approved,
                              @RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return bookingService.approveOrReject(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable @Positive long bookingId,
                              @RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllByBookerId(@RequestParam(defaultValue = "ALL") String state,
                                              @RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        return bookingService.findAllByBookerId(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerId(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size) {
        return bookingService.findAllByOwnerId(state, userId, from, size);
    }
}