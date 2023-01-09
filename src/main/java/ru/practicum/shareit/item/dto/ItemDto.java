package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @BooleanFlag
    private Boolean available;
}
