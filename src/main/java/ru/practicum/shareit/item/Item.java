package ru.practicum.shareit.item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {

    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    @BooleanFlag
    private Boolean available;
    private long owner;
}
