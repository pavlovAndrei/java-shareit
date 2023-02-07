package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

@Data
@Builder
public class BookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDto item;

    private UserDto booker;

    private Status status;
}
