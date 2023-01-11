package ru.practicum.shareit.item.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

@Data
@Builder
public class ItemGetDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @BooleanFlag
    @NotNull
    private Boolean available;

    private BookingDtoForItem lastBooking;

    private BookingDtoForItem nextBooking;

    private List<CommentDto> comments;
}
