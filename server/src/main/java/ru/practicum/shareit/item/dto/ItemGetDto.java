package ru.practicum.shareit.item.dto;

import java.util.List;

import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

@Data
@Builder
public class ItemGetDto {

    private Long id;

    private String name;

    private String description;

    @BooleanFlag
    private Boolean available;

    private BookingDtoForItem lastBooking;

    private BookingDtoForItem nextBooking;

    private List<CommentDto> comments;
}
