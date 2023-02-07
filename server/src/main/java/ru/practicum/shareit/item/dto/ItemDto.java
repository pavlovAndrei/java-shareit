package ru.practicum.shareit.item.dto;

import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    @BooleanFlag
    private Boolean available;

    private Long ownerId;

    private Long requestId;
}
