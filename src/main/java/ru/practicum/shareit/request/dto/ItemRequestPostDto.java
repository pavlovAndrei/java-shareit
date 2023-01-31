package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ItemRequestPostDto {

    @NotBlank
    private String description;
}
