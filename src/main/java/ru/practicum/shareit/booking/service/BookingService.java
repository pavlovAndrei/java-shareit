package ru.practicum.shareit.booking.service;

import java.util.List;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;

public interface BookingService {

    List<BookingDto> findAllByBookerId(String state, long userId, Integer offset, Integer size);

    List<BookingDto> findAllByOwnerId(String state, long userId, Integer offset, Integer size);

    BookingDto getById(long bookingId, long userId);

    BookingDto add(long userId, BookingPostDto bookingPostDto);

    BookingDto approveOrReject(long bookingId, boolean approved, long userId);
}
