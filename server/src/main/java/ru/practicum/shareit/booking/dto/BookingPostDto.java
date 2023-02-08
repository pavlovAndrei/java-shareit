package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingPostDto {

    private long itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}
