package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Builder;
import lombok.Data;

import ru.practicum.shareit.booking.valid.ValidDateRange;

@Data
@Builder
@ValidDateRange
public class BookingPostDto {

    @NotNull
    @Positive
    private long itemId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;
}
